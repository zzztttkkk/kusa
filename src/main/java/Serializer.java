import java.util.*;

class Serializer {
    static Class<?> CollectionCls = Collection.class;
    static Class<?> MapCls = Map.class;

    static JsonItem collectionToJson(Collection<?> collection) {
        JsonArray ary = new JsonArray();
        ArrayList<JsonItem> aryV = ary.expose();
        for (Object item : collection) {
            aryV.add(toJson(item));
        }
        return ary;
    }

    static JsonItem mapToJson(Map<?, ?> map) {
        JsonObject ele = new JsonObject();
        HashMap<String, JsonItem> eleV = ele.expose();

        for (Object key : map.keySet()) {
            eleV.put(key.toString(), toJson(map.get(key)));
        }

        return ele;
    }

    static JsonItem toJson(Object obj) {
        if (obj == null) {
            return JsonNull.nil;
        }

        Class<?> cls = obj.getClass();

        if (cls.isEnum()) {
            return new JsonString(obj.toString());
        }

        Converter converter = converters.get(cls);
        if (converter != null) {
            return converter.toJson(obj);
        }

        if (CollectionCls.isAssignableFrom(cls)) {
            return collectionToJson((Collection<?>) obj);
        }

        if (MapCls.isAssignableFrom(cls)) {
            return mapToJson((Map<?, ?>) obj);
        }

        String clsName = cls.getName();
        ArrayList<Fan> fields = reflect.getFields(cls);
        if (fields.size() < 1) {
            return JsonNull.nil;
        }

        JsonObject ele = new JsonObject();
        HashMap<String, JsonItem> eleV = ele.expose();

        JsonString typeV = new JsonString();
        typeV.value = clsName;
        eleV.put("@type", typeV);

        for (Fan field : fields) {
            eleV.put(field.getKey(), toJson(field.getValue(obj)));
        }
        return ele;
    }
}
