import exceptions.EncodeException;
import exceptions.ValueException;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.*;

public class Encoder {
    private static final boolean[] escapeMap = new boolean[127];
    private static final byte[] True;
    private static final byte[] False;
    private static final byte[] Null;

    static {
        for (char ch : "\n\t\r\\\b\f\"".toCharArray()) {
            escapeMap[ch] = true;
        }

        True = "true".getBytes();
        False = "false".getBytes();
        Null = "null".getBytes();
    }

    private final PrintStream writer;
    private final boolean sortKey;
    private final StringBuilder builder;

    public Encoder(OutputStream writer) {
        this.writer = new PrintStream(writer);
        sortKey = false;
        builder = new StringBuilder();
    }

    public Encoder(OutputStream writer, boolean sortKey) {
        this.writer = new PrintStream(writer);
        this.sortKey = sortKey;
        builder = new StringBuilder();
    }

    public String escape(String raw) {
        builder.setLength(0);

        for (char ch : raw.toCharArray()) {
            if (ch <= 127 && escapeMap[ch]) {
                builder.append('\n');
            }
            builder.append(ch);
        }

        return builder.toString();
    }

    void ce() {
        if (writer.checkError()) {
            throw new EncodeException();
        }
    }

    void write(char b) {
        writer.write(b);
        ce();
    }

    void print(String str) {
        writer.write('"');
        writer.print(escape(str));
        writer.write('"');
        ce();
    }

    void writeTrue() {
        try {
            writer.write(True);
        } catch (IOException exception) {
            throw new EncodeException();
        }
    }

    void writeFalse() {
        try {
            writer.write(False);
        } catch (IOException exception) {
            throw new EncodeException();
        }
    }

    void writeNull() {
        try {
            writer.write(Null);
        } catch (IOException exception) {
            throw new EncodeException();
        }
    }

    public void encode(JsonItem item) {
        int ind;
        int last;

        switch (item.type) {
            case Obj -> {
                write('{');

                HashMap<String, JsonItem> m = item.Object().expose();
                if (m == null) {
                    throw new ValueException();
                }

                Collection<String> keys;

                if (sortKey) {
                    keys = new ArrayList<>(m.keySet());
                    Collections.sort((ArrayList<String>) keys);
                } else {
                    keys = m.keySet();
                }

                ind = 0;
                last = keys.size() - 1;
                for (String key : keys) {
                    print(key);
                    write(':');
                    encode(m.get(key));
                    if (ind < last) {
                        write(',');
                    }
                    ind++;
                }

                write('}');
            }
            case Ary -> {
                write('[');

                ArrayList<JsonItem> l = item.Array().expose();
                if (l == null) {
                    throw new ValueException();
                }

                ind = 0;
                last = l.size() - 1;
                for (JsonItem ji : l) {
                    encode(ji);
                    if (ind < last) {
                        write(',');
                    }
                    ind++;
                }
                write(']');
            }
            case Bol -> {
                if (item.Boolean().isTrue()) {
                    writeTrue();
                } else {
                    writeFalse();
                }
            }
            case Nil -> {
                writeNull();
            }
            case Str -> {
                print(item.String().getString());
            }
            case Num -> {
                writer.print(item.Number().toStr());
                ce();
            }
        }
    }
}
