import exceptions.ClassException;

import java.lang.reflect.Field;

class Fan {
    Field raw;
    String name;
    String alias;

    Fan(Field field) {
        raw = field;
        name = field.getName();
        alias = "";
        raw.setAccessible(true);
    }

    String getKey() {
        if (!alias.isEmpty()) {
            return alias;
        }
        return name;
    }

    Object getValue(Object obj) {
        try {
            return raw.get(obj);
        } catch (IllegalAccessException e) {
            throw new ClassException();
        }
    }

    void setValue(Object ele, Object value) {
        try {
            raw.set(ele, value);
        } catch (IllegalAccessException e) {
            throw new ClassException();
        }
    }
}
