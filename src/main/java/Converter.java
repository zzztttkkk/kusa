public interface Converter {
    JsonItem toJson(Object object);

    Object toObject(JsonItem item);
}
