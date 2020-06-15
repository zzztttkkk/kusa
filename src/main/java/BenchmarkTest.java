import org.junit.jupiter.api.RepeatedTest;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class BenchmarkTest {
    @RepeatedTest(100)
    void kusa() throws IOException {
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
        }
    }
}
