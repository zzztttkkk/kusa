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


}
