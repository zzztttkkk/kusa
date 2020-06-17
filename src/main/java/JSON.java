import java.io.IOException;
import java.io.OutputStream;
import java.io.Reader;

public class JSON {
    private JSON() {
    }

    public static JsonItem parse(String text) {
        return Decoder.decode(text);
    }

    public static JsonItem parse(Reader reader) throws IOException {
        Decoder decoder = new Decoder();

        char[] buf = new char[128];
        int len;
        while ((len = reader.read(buf)) != -1) {
            for (int i = 0; i < len; i++) {
                decoder.feed(buf[i]);
            }
        }
        return decoder.getResult();
    }

    public static String stringify(JsonItem item) {
        return Encoder.stringify(item);
    }

    public static void stringifyTo(JsonItem item, OutputStream os) throws IOException {
        Encoder encoder = new Encoder(os);
        encoder.encode(item);
        encoder.flush();
    }
}
