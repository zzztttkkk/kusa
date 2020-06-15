import exceptions.ValueException;

public class JsonString extends JsonItem {
    JsonString() {
        type = Types.Str;
    }

    public String getString() {
        if (value == null) {
            throw new ValueException();
        }
        return (String) (value);
    }
}
