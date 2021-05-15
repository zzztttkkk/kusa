public class JsonNumber extends JsonItem {
	private boolean isFloat;

	JsonNumber(boolean isFloat) {
		type = Types.Num;
		this.isFloat = isFloat;
	}

	JsonNumber(long val) {
		type = Types.Num;
		value = val;
	}

	JsonNumber(double val) {
		type = Types.Num;
		value = val;
		isFloat = true;
	}

	public boolean isFloat() {
		return isFloat;
	}

	public long getLong() {
		notNullValue();
		return (long) (value);
	}

	public double getDouble() {
		notNullValue();
		return (double) (value);
	}

	public void setValue(long val) {
		value = val;
	}

	public void setValue(double val) {
		value = val;
	}

	String toJson() {
		notNullValue();
		return value.toString();
	}
}
