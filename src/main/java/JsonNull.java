public final class JsonNull extends JsonItem {
    static final JsonNull nil = new JsonNull();

    JsonNull() {
        type = Types.Nil;
    }

    public static JsonNull getInstance() {
        return nil;
    }
}
