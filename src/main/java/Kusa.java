import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

public class Kusa {
    private Kusa() {
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
        return Encoder.stringify(item, false);
    }

    public static String stringify(JsonItem item, boolean sortKey) {
        return Encoder.stringify(item, sortKey);
    }

    public static void stringify(JsonItem item, Writer writer) throws IOException {
        Encoder encoder = new Encoder(writer);
        encoder.encode(item);
        encoder.flush();
    }

    public static void stringify(JsonItem item, Writer writer, boolean sortKey) throws IOException {
        Encoder encoder = new Encoder(writer, sortKey);
        encoder.encode(item);
        encoder.flush();
    }

    public static String serialize(Object object) {
        JsonItem item = Serializer.toJson(object);
        return stringify(item);
    }

    public static String serialize(Object object, boolean sortKey) {
        JsonItem item = Serializer.toJson(object);
        return stringify(item, sortKey);
    }
}
