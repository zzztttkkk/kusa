public class JsonNumber extends JsonItem {
    private boolean isfloat;

    JsonNumber(boolean isFloat) {
        type = Types.Num;
        this.isfloat = isFloat;
    }

    JsonNumber(long val) {
        type = Types.Num;
        value = val;
    }

    JsonNumber(double val) {
        type = Types.Num;
        value = val;
        isfloat = true;
    }

    public boolean isFloat() {
        return isfloat;
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
