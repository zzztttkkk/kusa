import java.util.*;

class Serializer {
	static Class<?> CollectionCls = Collection.class;
	static Class<?> MapCls = Map.class;

	static JsonItem collectionToJson(Collection<?> collection) {
		JsonArray ary = new JsonArray();
		ArrayList<JsonItem> aryV = ary.expose();
		if (aryV == null) {
			aryV = new ArrayList<>();
			ary.value = aryV;
		}
		for (Object item : collection) {
			aryV.add(toJson(item));
		}
		return ary;
	}

	static JsonItem mapToJson(Map<?, ?> map) {
		JsonObject ele = new JsonObject();
		HashMap<String, JsonItem> eleV = ele.expose();

		for (Map.Entry<?, ?> entry : map.entrySet()) {
			eleV.put(entry.getKey().toString(), toJson(entry.getValue()));
		}
		return ele;
	}

	static JsonItem toJson(Object obj) {
		if (obj == null) {
			return JsonNull.getInstance();
		}

		Class<?> cls = obj.getClass();

		if (cls.isEnum()) {
			return new JsonString(obj.toString());
		}

		Converter converter = converters.get(cls);
		if (converter != null) {
			return converter.toJson(obj);
		}

		if (MapCls.isAssignableFrom(cls)) {
			return mapToJson((Map<?, ?>) obj);
		}

		if (CollectionCls.isAssignableFrom(cls)) {
			return collectionToJson((Collection<?>) obj);
		}

		JsonObject ele = new JsonObject();
		ArrayList<FieldInfo> fields = reflect.getFieldInfos(cls);
		if (fields.size() < 1) {
			return ele;
		}

		HashMap<String, JsonItem> eleV = ele.expose();
		if (eleV == null) {
			ele.value = new HashMap<>();
			eleV = ele.expose();
		}
		for (FieldInfo field : fields) {
			eleV.put(field.getKey(), toJson(field.getValue(obj)));
		}
		return ele;
	}
}
