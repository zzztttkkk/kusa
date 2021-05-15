import java.util.HashMap;

public class JsonObject extends JsonItem {
	JsonObject() {
		type = Types.Obj;
		value = null;
	}

	public int size() {
		if (value == null) {
			return -1;
		}
		return expose().size();
	}

	public void put(String key, JsonItem item) {
		if (value == null) {
			value = new HashMap<String, JsonItem>();
		}
		expose().put(key, item);
	}

	public JsonItem get(String key) {
		if (value == null) {
			return null;
		}
		return expose().get(key);
	}

	@SuppressWarnings("unchecked")
	public HashMap<String, JsonItem> expose() {
		return ((HashMap<String, JsonItem>) (value));
	}

	public JsonItem remove(String key) {
		notNullValue();
		return expose().remove(key);
	}

	public void clear() {
		if (value == null) {
			return;
		}
		expose().clear();
	}
}
