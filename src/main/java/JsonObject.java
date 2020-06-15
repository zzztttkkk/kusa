import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("unchecked")
public class JsonObject extends JsonItem {
    JsonObject() {
        type = Types.Obj;
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
}
