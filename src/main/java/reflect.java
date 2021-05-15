import exceptions.ClassException;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

class reflect {
	static final HashMap<Class<?>, ArrayList<FieldInfo>> listCache = new HashMap<>();
	static final HashMap<Class<?>, HashMap<String, FieldInfo>> mapCache = new HashMap<>();

	static void doGetFields(Class<?> cls, ArrayList<FieldInfo> lst) {
		for (Field field : cls.getDeclaredFields()) {
			JsonAlias alias = field.getAnnotation(JsonAlias.class);
			if (alias != null && alias.value().equals("-")) {
				continue;
			}

			FieldInfo f = new FieldInfo(field);
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
		doGetFields(sc, lst);
	}

	static ArrayList<FieldInfo> getFieldInfos(Class<?> cls) {
		ArrayList<FieldInfo> lst = listCache.get(cls);
		if (lst != null) {
			return lst;
		}
		lst = new ArrayList<>();
		doGetFields(cls, lst);
		listCache.put(cls, lst);
		return lst;
	}

	static HashMap<String, FieldInfo> getFieldInfoMap(Class<?> cls) {
		HashMap<String, FieldInfo> map = mapCache.get(cls);
		if (map != null) {
			return map;
		}
		map = new HashMap<>();
		ArrayList<FieldInfo> lst = getFieldInfos(cls);
		for (FieldInfo fi : lst) {
			map.put(fi.getKey(), fi);
		}
		mapCache.put(cls, map);
		return map;
	}
}
