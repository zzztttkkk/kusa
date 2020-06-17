import exceptions.ValueException;

public class JsonString extends JsonItem {
    JsonString() {
        type = Types.Str;
    }

    public JsonString(String text) {
        type = Types.Str;
        value = text;
    }

    public JsonString set(String text) {
        value = text;
        return this;
    }

    public String get() {
        if (value == null) {
            throw new ValueException();
        }
        return (String) (value);
    }
}
