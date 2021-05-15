import exceptions.ParseException;

import java.util.*;

class Decoder {
	static boolean[] numChMap = new boolean[127];
	static boolean[] hexChMap = new boolean[127];

	static {
		for (char ch : "-+.eE0123456789".toCharArray()) {
			numChMap[ch] = true;
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
	char[] vBuf;
	int vLength;
	int vStatus;
	boolean vSigned;
	boolean vPointed;
	boolean vEed;

	// sep ,
	boolean passASep;

	// u escape
	int uStatus;
	char[] uCache;

	// err cache
	LinkedList<Character> errCharLL;
	StringBuilder errBuf;

	Decoder() {
		buffer = new StringBuilder();
		stack = new Stack<>();
		vStatus = -1;
		vBuf = new char[30];
		uStatus = -1;
		uCache = new char[4];
		errCharLL = new LinkedList<>();
		errBuf = new StringBuilder();
		expectValue = true;
	}


	void exit() {
		errBuf.setLength(0);
		for (char ch : errCharLL) {
			errBuf.append(ch);
		}
		throw new ParseException(errBuf.toString());
	}

	void feed(char ch) {
		errCharLL.add(ch);
		if (errCharLL.size() > 40) {
			errCharLL.pollFirst();
		}

		if (!inString && Character.isWhitespace(ch)) {
			return;
		}

		if (done) {
			exit();
			return;
		}

		if (inString) {
			if (uStatus > -1) {
				if (!hexChMap[ch]) {
					exit();
					return;
				}
				uCache[uStatus] = ch;
				uStatus++;
				if (uStatus == 4) {
					uStatus = -1;
					int iv = Integer.parseInt(String.valueOf(uCache), 16);
					if (iv > 65535) {
						exit();
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
					uStatus = 0;
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

		if (expect != 0 && expect != ch) {
			boolean isLastItem = expect == ',' && (ch == '}' || ch == ']');
			if (!isLastItem) {
				exit();
			}
		}
		expect = 0;

		switch (ch) {
			case '"' -> inString = true;
			case '{' -> onObjBegin();
			case '}' -> onObjEnd();
			case '[' -> onAryBegin();
			case ']' -> onAryEnd();
			case ',' -> onEleSep();
			case ':' -> onKVSep();
			default -> {
				if (!expectValue) {
					exit();
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

		JsonItem item = new JsonString(val);
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
						exit();
						return;
					}
					expect = 0;
					keyValid = false;
					top.Object().put(key, item);
				}
				default -> {
					exit();
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
		appendItem(item);
	}

	void onObjEnd() {
		endNumber();
		if (passASep) {
			exit();
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
		appendItem(item);
	}

	void onAryEnd() {
		endNumber();
		if (passASep) {
			exit();
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
		if (vStatus != 3 || vLength == 0) {
			return;
		}

		String $ = String.copyValueOf(vBuf, 0, vLength);

		try {
			if (vPointed) {
				double v = Double.parseDouble($);
				JsonItem item = new JsonNumber(true);
				item.value = v;
				appendItem(item);
			} else {
				long v = Long.parseLong($);
				JsonItem item = new JsonNumber(false);
				item.value = v;
				appendItem(item);
			}
		} catch (NumberFormatException exp) {
			exit();
			return;
		}
		expect = 0;
		resetVal();
	}

	void writeValue(char ch) {
		if (vLength == 0) {
			switch (ch) {
				case 't' -> {
					vStatus = 0;
					vLength++;
				}
				case 'f' -> {
					vStatus = 1;
					vLength++;
				}
				case 'n' -> {
					vStatus = 2;
					vLength++;
				}
				default -> {
					vStatus = 3;
					writeNumber(ch);
				}
			}
			return;
		}

		switch (vStatus) {
			case 0 -> writeTrue(ch);
			case 1 -> writeFalse(ch);
			case 2 -> writeNull(ch);
			default -> writeNumber(ch);
		}
	}

	void writeTrue(char ch) {
		switch (vLength) {
			case 1 -> {
				if (ch != 'r') {
					exit();
					return;
				}
				vLength++;
			}
			case 2 -> {
				if (ch != 'u') {
					exit();
					return;
				}
				vLength++;
			}
			case 3 -> {
				if (ch != 'e') {
					exit();
					return;
				}
				appendItem(JsonBoolean.True());
				vLength = 0;
			}
			default -> exit();
		}
	}

	void writeFalse(char ch) {
		switch (vLength) {
			case 1 -> {
				if (ch != 'a') {
					exit();
					return;
				}
				vLength++;
			}
			case 2 -> {
				if (ch != 'l') {
					exit();
					return;
				}
				vLength++;
			}
			case 3 -> {
				if (ch != 's') {
					exit();
					return;
				}
				vLength++;
			}
			case 4 -> {
				if (ch != 'e') {
					exit();
					return;
				}
				appendItem(JsonBoolean.False());
				vLength = 0;
			}
			default -> exit();
		}
	}

	void writeNull(char ch) {
		switch (vLength) {
			case 1 -> {
				if (ch != 'u') {
					exit();
					return;
				}
				vLength++;
			}
			case 2 -> {
				if (ch != 'l') {
					exit();
					return;
				}
				vLength++;
			}
			case 3 -> {
				if (ch != 'l') {
					exit();
					return;
				}
				appendItem(JsonNull.nil);
				vLength = 0;
			}
			default -> exit();
		}
	}

	void writeNumber(char ch) {
		if (ch > 127 || !numChMap[ch] || vLength >= 26) {
			if (vLength == 0 || vLength >= 26) {
				exit();
				return;
			}

			endNumber();

			feed(ch);
			return;
		}

		switch (ch) {
			case '.' -> {
				if (!vPointed) {
					vPointed = true;
				} else {
					exit();
					return;
				}
			}
			case 'e', 'E' -> {
				if (!vEed) {
					vEed = true;
				} else {
					exit();
					return;
				}
			}
			case '-', '+' -> {
				if (vLength != 0 || vSigned) {
					exit();
					return;
				}
				vSigned = true;
			}
		}

		vBuf[vLength] = ch;
		vLength++;
	}

	void resetVal() {
		vStatus = -1;
		vSigned = false;
		vEed = false;
		vLength = 0;
		vPointed = false;
	}

	JsonItem getResult() {
		if (!done) {
			endNumber();
		}
		return result;
	}

	void reset() {
		done = false;
		inString = false;
		inEscape = false;
		buffer.setLength(0);
		stack.clear();
		result = null;
		keyValid = false;
		key = "";
		expect = 0;
		expectValue = true;
		vLength = 0;
		vStatus = 0;
		vSigned = false;
		vPointed = false;
		vEed = false;
		passASep = false;
		uStatus = -1;
		errCharLL.clear();
		errBuf.setLength(0);
	}

	static JsonItem decode(String text) {
		Decoder decoder = new Decoder();
		for (char ch : text.toCharArray()) {
			decoder.feed(ch);
		}
		return decoder.getResult();
	}
}
