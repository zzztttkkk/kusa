import java.util.HashMap;

@SuppressWarnings("unchecked")
public class JsonObject extends JsonItem {
    JsonObject() {
        type = Types.Obj;
        value = new HashMap<String, JsonItem>();
    }

    public void put(String key, JsonItem item) {
        ((HashMap<String, JsonItem>) (value)).put(key, item);
    }

    public JsonItem get(String key) {
        return ((HashMap<String, JsonItem>) (value)).get(key);
    }

    public HashMap<String, JsonItem> expose() {
        return ((HashMap<String, JsonItem>) (value));
    }

    public JsonItem remove(String key) {
        return ((HashMap<String, JsonItem>) (value)).remove(key);
    }

    public void clear() {
        expose().clear();
    }
}
