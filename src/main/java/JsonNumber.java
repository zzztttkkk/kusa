import exceptions.ValueException;

import java.util.HashMap;

public class JsonNumber extends JsonItem {
    static final HashMap<Integer, Boolean> m = new HashMap<>();

    static {
        m.put(((Object) (Short.parseShort("0"))).getClass().hashCode(), true);
        m.put(((Object) (Integer.parseInt("0"))).getClass().hashCode(), true);
        m.put(((Object) (Long.parseLong("0"))).getClass().hashCode(), true);
        m.put(((Object) (Float.parseFloat("0"))).getClass().hashCode(), true);
        m.put(((Object) (Double.parseDouble("0"))).getClass().hashCode(), true);
    }

    JsonNumber() {
        type = Types.Num;
    }

    private void cn() {
        if (value == null) {
            throw new ValueException();
        }
    }

    public short toShort() {
        cn();
        return (short) (value);
    }

    public int toInt() {
        cn();
        return (int) (value);
    }

    public long toLong() {
        cn();
        return (long) (value);
    }

    public float toFloat() {
        cn();
        return (float) (value);
    }

    public double toDouble() {
        cn();
        return (double) (value);
    }

    public void setValue(short val) {
        value = val;
    }

    public void setValue(int val) {
        value = val;
    }

    public void setValue(long val) {
        value = val;
    }

    public void setValue(float val) {
        value = val;
    }

    public void setValue(double val) {
        value = val;
    }

    String toStrX() {
        cn();
        Boolean v = m.get(value.getClass().hashCode());
        if (v == null || !v) {
            throw new ValueException();
        }
        return value.toString();
    }
}
