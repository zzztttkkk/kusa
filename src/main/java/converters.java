import java.util.Date;
import java.util.HashMap;

class converters {
	static HashMap<Class<?>, Converter> converterMap = new HashMap<>();

	static void register(Class<?> cls, Converter converter) {
		converterMap.put(cls, converter);
	}

	static Converter get(Class<?> cls) {
		return converterMap.get(cls);
	}

	static {
		register(
				Byte.class,
				new Converter() {
					@Override
					public JsonItem toJson(Object object) {
						return new JsonNumber(((Number) (object)).longValue());
					}

					@Override
					public Object toObject(JsonItem item) {
						if (item == null || item == JsonNull.nil) {
							return 0;
						}
						return (byte) (item.Number().getLong());
					}
				}
		);

		register(
				Short.class,
				new Converter() {
					@Override
					public JsonItem toJson(Object object) {
						if (object == null) {
							return JsonNull.getInstance();
						}
						return new JsonNumber(((Number) (object)).longValue());
					}

					@Override
					public Object toObject(JsonItem item) {
						if (item == null || item == JsonNull.nil) {
							return 0;
						}
						return (short) (item.Number().getLong());
					}
				}
		);

		register(
				Integer.class,
				new Converter() {
					@Override
					public JsonItem toJson(Object object) {
						if (object == null) {
							return JsonNull.getInstance();
						}
						return new JsonNumber(((Number) (object)).longValue());
					}

					@Override
					public Object toObject(JsonItem item) {
						if (item == null || item == JsonNull.nil) {
							return 0;
						}
						return (int) (item.Number().getLong());
					}
				}
		);

		register(
				Long.class,
				new Converter() {
					@Override
					public JsonItem toJson(Object object) {
						if (object == null) {
							return JsonNull.getInstance();
						}
						return new JsonNumber((long) (object));
					}

					@Override
					public Object toObject(JsonItem item) {
						if (item == null || item == JsonNull.nil) {
							return 0;
						}
						return item.Number().getLong();
					}
				}
		);

		register(
				Character.class,
				new Converter() {
					@Override
					public JsonItem toJson(Object object) {
						if (object == null) {
							return JsonNull.getInstance();
						}
						return new JsonNumber(((Number) (int) (char) (object)).longValue());
					}

					@Override
					public Object toObject(JsonItem item) {
						if (item == null || item == JsonNull.nil) {
							return 0;
						}
						return (char) (int) item.Number().getLong();
					}
				}
		);

		register(
				Float.class,
				new Converter() {
					@Override
					public JsonItem toJson(Object object) {
						if (object == null) {
							return JsonNull.getInstance();
						}
						return new JsonNumber(((Number) (object)).doubleValue());
					}

					@Override
					public Object toObject(JsonItem item) {
						if (item == null || item == JsonNull.nil) {
							return 0;
						}
						return (float) item.Number().getDouble();
					}
				}
		);

		register(
				Double.class,
				new Converter() {
					@Override
					public JsonItem toJson(Object object) {
						if (object == null) {
							return JsonNull.getInstance();
						}
						return new JsonNumber((double) (object));
					}

					@Override
					public Object toObject(JsonItem item) {
						if (item == null || item == JsonNull.nil) {
							return 0;
						}
						return item.Number().getDouble();
					}
				}
		);

		register(
				Date.class,
				new Converter() {
					@Override
					public JsonItem toJson(Object object) {
						if (object == null) {
							return JsonNull.getInstance();
						}
						return new JsonNumber(((Date) (object)).getTime());
					}

					@Override
					public Object toObject(JsonItem item) {
						if (item == null || item == JsonNull.nil) {
							return new Date(0);
						}
						return new Date(item.Number().getLong());
					}
				}
		);

		register(
				String.class,
				new Converter() {
					@Override
					public JsonItem toJson(Object object) {
						if (object == null) {
							return JsonNull.getInstance();
						}
						return new JsonString((String) object);
					}

					@Override
					public Object toObject(JsonItem item) {
						if (item == null || item == JsonNull.nil) {
							return "";
						}
						return item.String().get();
					}
				}
		);

		register(
				Boolean.class,
				new Converter() {
					@Override
					public JsonItem toJson(Object object) {
						if (object == null) {
							return JsonNull.getInstance();
						}
						if ((boolean) object) {
							return JsonBoolean.T;
						}
						return JsonBoolean.F;
					}

					@Override
					public Object toObject(JsonItem item) {
						if (item == null || item == JsonNull.nil) {
							return false;
						}
						return item == JsonBoolean.T;
					}
				}
		);
	}
}
