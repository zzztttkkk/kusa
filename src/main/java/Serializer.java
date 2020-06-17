import java.util.ArrayList;
import java.util.HashMap;

class Serializer {
    static JsonItem toJson(Object obj) {
        switch (obj.getClass().getName()) {
            case "java.lang.Integer", "java.lang.Short", "java.lang.Long" -> {
                return new JsonNumber(((Number) (obj)).longValue());
            }
            case "java.lang.Double", "java.lang.Float" -> {
                return new JsonNumber(((Number) (obj)).doubleValue());
            }
            case "java.lang.Boolean" -> {
                if ((boolean) (obj)) {
                    return JsonBoolean.T;
                }
                return JsonBoolean.F;
            }
            case "java.lang.String" -> {
                JsonString ele = new JsonString();
                ele.value = obj;
                return ele;
            }
            case "java.util.HashMap" -> {
                JsonObject ele = new JsonObject();
                HashMap<String, JsonItem> eleV = ele.expose();

                HashMap<?, ?> m = (HashMap<?, ?>) (obj);
                for (Object key : m.keySet()) {
                    eleV.put(key.toString(), toJson(m.get(key)));
                }

                return ele;
            }
            case "java.util.ArrayList" -> {
                JsonArray ary = new JsonArray();
                ArrayList<JsonItem> aryV = ary.expose();

                ArrayList<?> a = (ArrayList<?>) (obj);
                for (Object item : a) {
                    aryV.add(toJson(item));
                }

                return ary;
            }
            default -> {
                String clsName = obj.getClass().getName();
                ArrayList<Fan> fields = reflect.getFields(clsName);
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
    }
}
