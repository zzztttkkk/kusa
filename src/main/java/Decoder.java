import exceptions.ParseException;

import java.util.*;

public class Decoder {
    static boolean[] numChMap = new boolean[127];
    static boolean[] wsChMap = new boolean[127];
    static boolean[] hexChMap = new boolean[127];

    static {
        for (char ch : "-+.eE0123456789".toCharArray()) {
            numChMap[ch] = true;
        }

        for (char ch : "\r\n \t".toCharArray()) {
            wsChMap[ch] = true;
        }

        for (char ch : "abcdefABCDEF0123456789".toCharArray()) {
            hexChMap[ch] = true;
        }
    }

    boolean done;

    boolean inString;
    boolean inEscape;
    StringBuilder buffer;

    Stack<JsonItem> stack;
    JsonItem result;

    boolean keyValid;
    String key;
    char expect;
    boolean expectValue;

    // number, null, true, false
    char[] val;
    int length;
    int status;
    boolean signed;
    boolean pointed;
    boolean eed;

    // sep ,
    boolean passASep;

    // u escape
    int ustatus;
    char[] ucache;

    // err cache
    LinkedList<Character> ecq;
    StringBuilder ebuilder;

    public Decoder() {
        buffer = new StringBuilder();
        stack = new Stack<>();
        val = new char[30];
        ustatus = -1;
        ucache = new char[4];
        ecq = new LinkedList<>();
        ebuilder = new StringBuilder();
    }

    boolean isSpace(char ch) {
        if (ch > 127) {
            return false;
        }
        return wsChMap[ch];
    }

    void te() {
        ebuilder.setLength(0);
        for (char ch : ecq) {
            ebuilder.append(ch);
        }
        throw new ParseException(ebuilder.toString());
    }

    public void feed(char ch) {
        ecq.add(ch);
        if (ecq.size() > 40) {
            ecq.pollFirst();
        }

        if (!inString && isSpace(ch)) {
            return;
        }

        if (done) {
            te();
            return;
        }

        if (inString) {
            if (ustatus > -1) {
                if (!hexChMap[ch]) {
                    te();
                    return;
                }
                ucache[ustatus] = ch;
                ustatus++;
                if (ustatus == 4) {
                    ustatus = -1;
                    int iv = Integer.parseInt(String.valueOf(ucache), 16);
                    if (iv > 65535) {
                        te();
                        return;
                    }

                    buffer.append((char) iv);
                }

                return;
            }

            if (!inEscape && ch == '\\') {
                inEscape = true;
                return;
            }

            if (inEscape) {
                inEscape = false;
                if (ch == 'u') {
                    ustatus = 0;
                    return;
                }
                buffer.append(ch);
                return;
            }

            if (ch == '"') {
                inString = false;
                onStringEnd();
                return;
            }

            buffer.append(ch);
            return;
        }

        if (expect != 0) {
            if (expect != ch) {
                if (expect != ',') {
                    te();
                    return;
                } else {
                    if (ch != ']' && ch != '}') {
                        te();
                        return;
                    }
                }
            }
        }

        switch (ch) {
            case '"' -> {
                if (expect == '"') {
                    expect = 0;
                }
                inString = true;
            }
            case '{' -> {
                if (expect == '{') {
                    expect = 0;
                }
                onObjBegin();
            }
            case '}' -> {
                if (expect == ',') {
                    expect = 0;
                }
                onObjEnd();
            }
            case '[' -> {
                onAryBegin();
            }
            case ']' -> {
                if (expect == ',') {
                    expect = 0;
                }
                onAryEnd();
            }
            case ',' -> {
                if (expect == ',') {
                    expect = 0;
                }
                onEleSep();
            }
            case ':' -> {
                if (expect == ':') {
                    expect = 0;
                }
                onKVSep();
            }
            default -> {
                if (!expectValue) {
                    te();
                    return;
                }
                writeValue(ch);
            }
        }
    }

    void onStringEnd() {
        String val = buffer.toString();
        buffer.setLength(0);

        if (!stack.empty()) {
            JsonItem top = stack.peek();
            if (top.type == Types.Obj) {
                if (!keyValid) {
                    keyValid = true;
                    key = val;
                    expect = ':';
                    expectValue = false;
                    return;
                }
            }
        }

        JsonItem item = new JsonString();
        item.value = val;
        appendItem(item);
    }

    void appendItem(JsonItem item) {
        expectValue = false;
        if (passASep) {
            passASep = false;
        }

        if (!stack.empty()) {
            JsonItem top = stack.peek();
            switch (top.type) {
                case Ary -> {
                    top.Array().add(item);
                    expect = ',';
                    expectValue = true;
                }
                case Obj -> {
                    if (!keyValid) {
                        te();
                        return;
                    }
                    expect = 0;
                    keyValid = false;
                    top.Object().put(key, item);
                }
                default -> {
                    te();
                    return;
                }
            }
        }

        switch (item.type) {
            case Obj -> {
                stack.push(item);
                expect = 0;
                expectValue = false;
                if (result == null) {
                    result = item;
                }
            }
            case Ary -> {
                stack.push(item);
                expect = 0;
                expectValue = true;
                if (result == null) {
                    result = item;
                }
            }
            default -> {
                expect = ',';
                expectValue = true;
                if (result == null) {
                    result = item;
                    done = true;
                }
            }
        }
    }

    void onObjBegin() {
        JsonItem item = new JsonObject();
        item.value = new HashMap<String, JsonItem>();
        appendItem(item);
    }

    void onObjEnd() {
        endNumber();
        if (passASep) {
            te();
            return;
        }

        stack.pop();
        if (stack.empty()) {
            done = true;
            return;
        }

        JsonItem top = stack.peek();
        if (top.type == Types.Ary) {
            expect = ',';
            expectValue = true;
        }
    }

    void onAryBegin() {
        JsonItem item = new JsonArray();
        item.value = new ArrayList<JsonItem>();
        appendItem(item);
    }

    void onAryEnd() {
        endNumber();
        if (passASep) {
            te();
            return;
        }

        stack.pop();
        if (stack.empty()) {
            done = true;
            return;
        }

        JsonItem top = stack.peek();
        if (top.type == Types.Ary) {
            expect = ',';
            expectValue = true;
        }
    }

    void onEleSep() {
        endNumber();
        passASep = true;
    }

    void onKVSep() {
        expect = 0;
        expectValue = true;
    }

    void endNumber() {
        if (status != 3 || length == 0) {
            return;
        }

        String $ = String.copyValueOf(val, 0, length);

        try {
            if ($.contains(".")) {
                double v = Double.parseDouble($);
                JsonItem item = new JsonNumber();
                item.value = v;
                appendItem(item);
            } else {
                long v = Long.parseLong($);
                JsonItem item = new JsonNumber();
                item.value = v;
                appendItem(item);
            }
        } catch (NumberFormatException exp) {
            te();
            return;
        }
        expect = 0;
        resetVal();
    }

    void writeValue(char ch) {
        if (length == 0) {
            switch (ch) {
                case 't' -> {
                    status = 0;
                    length++;
                }
                case 'f' -> {
                    status = 1;
                    length++;
                }
                case 'n' -> {
                    status = 2;
                    length++;
                }
                default -> {
                    status = 3;
                    writeNumber(ch);
                }
            }
            return;
        }

        switch (status) {
            case 0 -> {
                writeTrue(ch);
            }
            case 1 -> {
                writeFalse(ch);
            }
            case 2 -> {
                writeNull(ch);
            }
            default -> {
                writeNumber(ch);
            }
        }
    }

    void writeTrue(char ch) {
        switch (length) {
            case 1 -> {
                if (ch != 'r') {
                    te();
                    return;
                }
                length++;
            }
            case 2 -> {
                if (ch != 'u') {
                    te();
                    return;
                }
                length++;
            }
            case 3 -> {
                if (ch != 'e') {
                    te();
                    return;
                }
                appendItem(JsonBoolean.True());
                length = 0;
            }
            default -> {
                te();
            }
        }
    }

    void writeFalse(char ch) {
        switch (length) {
            case 1 -> {
                if (ch != 'a') {
                    te();
                    return;
                }
                length++;
            }
            case 2 -> {
                if (ch != 'l') {
                    te();
                    return;
                }
                length++;
            }
            case 3 -> {
                if (ch != 's') {
                    te();
                    return;
                }
                length++;
            }
            case 4 -> {
                if (ch != 'e') {
                    te();
                    return;
                }
                appendItem(JsonBoolean.False());
                length = 0;
            }
            default -> {
                te();
            }
        }
    }

    void writeNull(char ch) {
        switch (length) {
            case 1 -> {
                if (ch != 'u') {
                    te();
                    return;
                }
                length++;
            }
            case 2 -> {
                if (ch != 'l') {
                    te();
                    return;
                }
                length++;
            }
            case 3 -> {
                if (ch != 'l') {
                    te();
                    return;
                }
                appendItem(JsonNull.nil);
                length = 0;
            }
            default -> {
                te();
            }
        }
    }

    void writeNumber(char ch) {
        if (ch > 127 || !numChMap[ch] || length >= 26) {
            if (length == 0 || length >= 26) {
                te();
                return;
            }

            endNumber();

            feed(ch);
            return;
        }

        switch (ch) {
            case '.' -> {
                if (!pointed) {
                    pointed = true;
                } else {
                    te();
                    return;
                }
            }
            case 'e', 'E' -> {
                if (!eed) {
                    eed = true;
                } else {
                    te();
                    return;
                }
            }
            case '-', '+' -> {
                if (!signed) {
                    signed = true;
                } else {
                    te();
                }
            }
        }

        val[length] = ch;
        length++;
    }

    void resetVal() {
        signed = false;
        eed = false;
        length = 0;
        pointed = false;
    }

    public JsonItem getResult() {
        return result;
    }

    public void reset() {
        done = false;
        inString = false;
        inEscape = false;
        buffer.setLength(0);
        stack.clear();
        result = null;
        keyValid = false;
        key = "";
        expect = 0;
        expectValue = false;
        length = 0;
        status = 0;
        signed = false;
        pointed = false;
        eed = false;
        passASep = false;
        ustatus = -1;
        ecq.clear();
        ebuilder.setLength(0);
    }
}
