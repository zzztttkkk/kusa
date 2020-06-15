import java.util.ArrayList;

@SuppressWarnings("unchecked")
public class JsonArray extends JsonItem {
    JsonArray() {
        type = Types.Ary;
    }

    public void add(JsonItem item) {
        ((ArrayList<JsonItem>) (value)).add(item);
    }

    public ArrayList<JsonItem> expose() {
        return ((ArrayList<JsonItem>) (value));
    }

    public JsonItem get(int index) {
        return ((ArrayList<JsonItem>) (value)).get(index);
    }

    public JsonItem remove(int index) {
        return ((ArrayList<JsonItem>) (value)).remove(index);
    }
}
