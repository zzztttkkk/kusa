import exceptions.ClassException;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

class reflect {
    static final HashSet<String> whiteLst = new HashSet<>();
    static final HashSet<String> blackLst = new HashSet<>();
    static final HashMap<String, Class<?>> clzMap = new HashMap<>();
    static final HashMap<String, ArrayList<Fan>> fieldMap = new HashMap<>();

    static {
        whiteLst.add(Object.class.getName());

        whiteLst.add(Short.class.getName());
        whiteLst.add(Integer.class.getName());
        whiteLst.add(Long.class.getName());
        whiteLst.add(Double.class.getName());
        whiteLst.add(Float.class.getName());
        whiteLst.add(Boolean.class.getName());

        whiteLst.add(String.class.getName());

        whiteLst.add(HashMap.class.getName());
        whiteLst.add(ArrayList.class.getName());
    }

    static Class<?> getClass(String name) {
        if (blackLst.contains(name)) {
            throw new ClassException();
        }

        Class<?> cls;
        try {
            cls = Class.forName(name);
        } catch (ClassNotFoundException e) {
            throw new ClassException();
        }

        if (!whiteLst.contains(name)) {
            JsonReflectSafe safe = cls.getAnnotation(JsonReflectSafe.class);
            if (safe == null) {
                blackLst.add(name);
                throw new ClassException();
            }
            whiteLst.add(name);
        }
        return cls;
    }

    static void doGetFields(Class<?> cls, ArrayList<Fan> lst) {
        for (Field field : cls.getDeclaredFields()) {
            JsonAlias alias = field.getAnnotation(JsonAlias.class);
            if (alias != null && alias.value().equals("-")) {
                continue;
            }

            Fan f = new Fan(field);
            if (alias != null) {
                f.alias = alias.value();
            }
            lst.add(f);
        }

        Class<?> sc = cls.getSuperclass();
        if (sc == null || sc == Object.class) {
            return;
        }
        Class<?> scls = getClass(sc.getName());
        doGetFields(scls, lst);
    }

    static ArrayList<Fan> getFields(String name) {
        ArrayList<Fan> lst = fieldMap.get(name);
        if (lst != null) {
            return lst;
        }

        Class<?> cls = getClass(name);
        lst = new ArrayList<>();
        doGetFields(cls, lst);

        fieldMap.put(name, lst);

        return lst;
    }
}
