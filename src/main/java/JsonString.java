public class JsonString extends JsonItem {
    JsonString() {
        type = Types.Str;
    }

    public String toString() {
        if (value == null) {

        }
        return (String) (value);
    }
}
