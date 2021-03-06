import java.io.*;
import java.util.*;

class Encoder {
	private static final boolean[] escapeMap = new boolean[127];
	private static final char[] True;
	private static final char[] False;
	private static final char[] Null;

	static {
		for (char ch : "\\\"".toCharArray()) {
			escapeMap[ch] = true;
		}

		True = "true".toCharArray();
		False = "false".toCharArray();
		Null = "null".toCharArray();
	}

	private final Writer writer;
	private final boolean sortKey;

	Encoder(Writer writer) {
		this.writer = writer;
		sortKey = false;
	}

	Encoder(Writer writer, boolean sortKey) {
		this.writer = writer;
		this.sortKey = sortKey;
	}

	void escape(String raw) throws IOException {
		for (char ch : raw.toCharArray()) {
			if (ch <= 127 && escapeMap[ch]) {
				writer.write('\\');
			}
			writer.write(ch);
		}
	}

	void write(char b) throws IOException {
		writer.write(b);
	}

	void print(String str) throws IOException {
		writer.write('"');
		escape(str);
		writer.write('"');
	}

	void writeTrue() throws IOException {
		writer.write(True);
	}

	void writeFalse() throws IOException {
		writer.write(False);
	}

	void writeNull() throws IOException {
		writer.write(Null);
	}

	void encode(JsonItem item) throws IOException {
		int ind;
		int last;

		switch (item.type) {
			case Obj -> {
				write('{');

				HashMap<String, JsonItem> m = item.Object().expose();
				if (m != null) {
					Collection<Map.Entry<String, JsonItem>> entries;

					if (sortKey) {
						entries = new ArrayList<>(m.entrySet());
						((ArrayList<Map.Entry<String, JsonItem>>) entries).sort(Map.Entry.comparingByKey());
					} else {
						entries = m.entrySet();
					}

					ind = 0;
					last = entries.size() - 1;
					for (Map.Entry<String, JsonItem> entry : entries) {
						print(entry.getKey());
						write(':');
						encode(entry.getValue());
						if (ind < last) {
							write(',');
						}
						ind++;
					}
				}
				write('}');
			}
			case Ary -> {
				write('[');

				ArrayList<JsonItem> l = item.Array().expose();
				if (l != null) {
					ind = 0;
					last = l.size() - 1;
					for (JsonItem ji : l) {
						encode(ji);
						if (ind < last) {
							write(',');
						}
						ind++;
					}
				}
				write(']');
			}
			case Bol -> {
				if (item.Boolean().isTrue()) {
					writeTrue();
				} else {
					writeFalse();
				}
			}
			case Nil -> writeNull();
			case Str -> print(item.String().get());
			case Num -> writer.write(item.Number().toJson());
		}
	}

	void flush() throws IOException {
		writer.flush();
	}

	static String stringify(JsonItem item, boolean sortKey) {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		OutputStreamWriter writer = new OutputStreamWriter(os);

		Encoder encoder = new Encoder(writer, sortKey);
		try {
			encoder.encode(item);
			encoder.flush();
		} catch (IOException ignored) {
		}
		return os.toString();
	}
}
