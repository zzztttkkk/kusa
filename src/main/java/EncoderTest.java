import org.junit.jupiter.api.Test;

import java.io.*;
import java.nio.charset.StandardCharsets;

class EncoderTest {
    @Test
    void encode() throws IOException {
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        JsonItem doc;
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
            doc = decoder.result;
        }


        System.out.print(System.getenv("PROJECT_ROOT"));
        try (FileOutputStream v = new FileOutputStream(System.getenv("PROJECT_ROOT") + "/dist/v.json")) {
            Encoder encoder = new Encoder(v, false);

            encoder.encode(doc);
        }
    }
}