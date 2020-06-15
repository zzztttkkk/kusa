public final class JsonBoolean extends JsonItem {
    static final JsonBoolean T = new JsonBoolean();
    static final JsonBoolean F = new JsonBoolean();

    static {
        T.value = true;
        F.value = false;
    }

    JsonBoolean() {
        type = Types.Bol;
    }

    public boolean isTrue() {
        return this == T;
    }

    public boolean isFalse() {
        return this != T;
    }

    public static JsonBoolean False() {
        return F;
    }

    public static JsonBoolean True() {
        return T;
    }
}
