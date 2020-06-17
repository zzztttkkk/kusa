import exceptions.TypeCastException;
import exceptions.ValueException;

public class JsonItem {
    Types type;
    Object value;

    public JsonItem() {
    }

    public JsonBoolean Boolean() {
        if (type != Types.Bol) {
            throw new TypeCastException();
        }
        return (JsonBoolean) (this);
    }

    public JsonObject Object() {
        if (type != Types.Obj) {
            throw new TypeCastException();
        }
        return (JsonObject) (this);
    }

    public JsonArray Array() {
        if (type != Types.Ary) {
            throw new TypeCastException();
        }
        return (JsonArray) (this);
    }

    public JsonNumber Number() {
        if (type != Types.Num) {
            throw new TypeCastException();
        }
        return (JsonNumber) (this);
    }

    public JsonNull Null() {
        if (type != Types.Nil) {
            throw new TypeCastException();
        }
        return JsonNull.nil;
    }

    public JsonString String() {
        if (type != Types.Str) {
            throw new TypeCastException();
        }
        return (JsonString) (this);
    }

    void notNullValue() {
        if (value == null) {
            throw new ValueException();
        }
    }
}
