import java.util.ArrayList;

public class JsonArray extends JsonItem {
	public JsonArray() {
		type = Types.Ary;
		value = null;
	}

	public int size() {
		if (value == null) {
			return -1;
		}
		return expose().size();
	}

	public void add(JsonItem item) {
		if (value == null) {
			value = new ArrayList<JsonItem>();
		}
		expose().add(item);
	}

	public ArrayList<JsonItem> expose() {
		//noinspection unchecked
		return (ArrayList<JsonItem>) (value);
	}

	public JsonItem get(int index) {
		if (value == null) {
			return null;
		}
		return expose().get(index);
	}

	public JsonItem remove(int index) {
		notNullValue();
		return expose().remove(index);
	}
}
