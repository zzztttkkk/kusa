import exceptions.ClassException;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

class reflect {
    static final HashSet<Class<?>> whiteLst = new HashSet<>();
    static final HashSet<Class<?>> blackLst = new HashSet<>();
    static final HashMap<Class<?>, ArrayList<Fan>> fieldMap = new HashMap<>();

    static void isValid(Class<?> cls) {
        if (blackLst.contains(cls)) {
            throw new ClassException(cls.getName());
        }
        if (!whiteLst.contains(cls)) {
            JsonReflectSafe safe = cls.getAnnotation(JsonReflectSafe.class);
            if (safe == null) {
                blackLst.add(cls);
                System.out.println(cls);
                throw new ClassException(cls.getName());
            }
            whiteLst.add(cls);
        }
    }

    static void doGetFields(Class<?> cls, ArrayList<Fan> lst) {
        for (Field field : cls.getDeclaredFields()) {
            JsonAlias alias = field.getAnnotation(JsonAlias.class);
            if (alias != null && alias.value().equals("-")) {
                continue;
            }

            Fan f = new Fan(field);
            f.clsName = cls.getName();
            if (alias != null) {
                f.alias = alias.value();
            }
            lst.add(f);
        }

        Class<?> sc = cls.getSuperclass();
        if (sc == null || sc == Object.class) {
            return;
        }
        isValid(sc);
        doGetFields(sc, lst);
    }

    static ArrayList<Fan> getFields(Class<?> cls) {
        ArrayList<Fan> lst = fieldMap.get(cls);
        if (lst != null) {
            return lst;
        }
        isValid(cls);
        lst = new ArrayList<>();
        doGetFields(cls, lst);
        fieldMap.put(cls, lst);
        return lst;
    }
}
