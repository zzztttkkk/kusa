import org.junit.jupiter.api.Test;

import java.io.*;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

class AllTest {
    @org.junit.jupiter.api.Test
    void utf8() throws IOException {
        OutputStream os = new OutputStream() {
            private final CharBuffer builder = CharBuffer.allocate(1024);

            @Override
            public void write(int b) throws IOException {
                builder.append((char) (b));
            }

            public String toString() {
                builder.position(0);
                return builder.toString();
            }
        };

        Writer osw = new OutputStreamWriter(os, StandardCharsets.UTF_8);

        osw.write("见");
        osw.flush();

        System.out.println(os.toString());
    }

    @org.junit.jupiter.api.Test
    void decodeString() {
        JsonItem item = JSON.parse("12");
        System.out.println(item.Number().getLong());

        item = JSON.parse("[[[[[[]]]]], 1, true, 1.23]");
        System.out.println(item.Array().expose());

        item = JSON.parse("\"甜辣酱\u0048\"");
        System.out.println(item.String().get());

        item = JSON.parse("null");
        System.out.println(item.Null());

        item = JSON.parse("{}");
        System.out.println(item.Object().expose());
    }

}