import exceptions.TypeCastException;

public class JsonItem {
    Types type;
    Object value;


    public JsonItem() {
    }

    public JsonBoolean toBoolean() {
        if (type != Types.Bol) {
            throw new TypeCastException();
        }

        JsonBoolean jb = (JsonBoolean) (this);
        if ((boolean) jb.value) {
            return JsonBoolean.True();
        }
        return JsonBoolean.False();
    }

    public JsonNumber toNumber() {
        if (type != Types.Num) {
            throw new TypeCastException();
        }
        return (JsonNumber) (this);
    }

    public JsonObject toObject() {
        if (type != Types.Obj) {
            throw new TypeCastException();
        }
        return (JsonObject) (this);
    }

    public JsonArray toArray() {
        if (type != Types.Ary) {
            throw new TypeCastException();
        }
        return (JsonArray) (this);
    }

    public JsonNull toNull() {
        if (type != Types.Nil) {
            throw new TypeCastException();
        }
        return (JsonNull) (this);
    }

    public JsonString toStr() {
        if (type != Types.Str) {
            throw new TypeCastException();
        }
        return (JsonString) (this);
    }

    public static boolean isNull(JsonItem item) {
        return item == JsonNull.nil;
    }

    static JsonBoolean True() {
        return JsonBoolean.T;
    }

    static JsonBoolean False() {
        return JsonBoolean.F;
    }

    static JsonNull Null() {
        return JsonNull.nil;
    }

    public static boolean isTrue(JsonItem item) {
        return item == JsonBoolean.T;
    }

    public static boolean isFalse(JsonItem item) {
        return item == JsonBoolean.F;
    }

    public static JsonItem getByPath(JsonItem item, String path) {
        JsonItem current = item;
        for (String key : path.split("\\.")) {
            switch (current.type) {
                case Ary -> {
                    current = current.toArray().get(Integer.parseInt(key));
                }
                case Obj -> {
                    current = current.toObject().get(key);
                }
                default -> {
                    return null;
                }
            }
        }
        return current;
    }
}
