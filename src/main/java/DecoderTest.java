import org.junit.jupiter.api.Test;

import java.io.*;
import java.nio.charset.StandardCharsets;

class DecoderTest {

    @Test
    void feed() {
        Decoder decoder = new Decoder();

        for (char ch : "{\"ss\" : 45}".toCharArray()) {
            decoder.feed(ch);
        }

        System.out.println(1);
    }

    @Test
    void file() {
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        try (InputStream is = classloader.getResourceAsStream("a.json")) {
            assert is != null;
            Reader reader = new InputStreamReader(is, StandardCharsets.UTF_8);
            char[] buf = new char[10];
            int len;
            Decoder decoder = new Decoder();
            while ((len = reader.read(buf)) != -1) {
                for (int i = 0; i < len; i++) {
                    decoder.feed(buf[i]);
                }
            }
            System.out.println(1);
        } catch (IOException ex) {
            System.out.println(ex.toString());
        }
    }

    @Test
    void x() {
        int a = 45;
        System.out.print(((Object) (a)).getClass().getName());
    }
}