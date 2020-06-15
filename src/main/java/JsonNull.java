public final class JsonNull extends JsonItem {
    static final JsonNull nil = new JsonNull();

    JsonNull() {
        type = Types.Nil;
    }

    static JsonNull Null() {
        return nil;
    }
}
