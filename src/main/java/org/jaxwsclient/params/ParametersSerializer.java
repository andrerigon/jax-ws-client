package org.jaxwsclient.params;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.common.math.DoubleMath;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class ParametersSerializer {

	@SuppressWarnings("unchecked")
	public static Map<String, Object> paramsFor(Object object, String name) throws IllegalAccessException, InvocationTargetException,
			NoSuchMethodException {
		if (object == null) {
			return Collections.emptyMap();
		}
		if (isWrapperType(object.getClass()) || isEnum(object)) {
			return simpleMapForValue(object, name);
		}
		if (isList(object)) {
			return serializedList((List<?>) object, name);
		}
		final Gson gson = new Gson();

		return fixNumbers((Map<String, Object>) gson.fromJson(gson.toJson(object), new TypeToken<Map<String, Object>>() {
		}.getType()));
	}

	@SuppressWarnings("unchecked")
	private static Map<String, Object> fixNumbers(Map<String, Object> map) {
		Map<String, Object> fixed = new HashMap<String, Object>();
		for (Entry<String, Object> e : map.entrySet()) {
			if (e.getValue() instanceof Number) {
				fixed.put(e.getKey(), lowestNumberImpl((Number) e.getValue()));
			}
			else if( e.getValue() instanceof Map){
				fixed.put(e.getKey(), fixNumbers((Map<String, Object>) e.getValue()));
			}
			else {
				fixed.put(e.getKey(), e.getValue());
			}
		}
		return fixed;
	}

	/**
	 * Gson serializing strategy is always deserializing to Double
	 * @param value
	 * @return
	 */
	private static Number lowestNumberImpl(Number value) {
		if (!DoubleMath.isMathematicalInteger(value.doubleValue())) {
			return value;
		}
		return value.longValue();
	}

	private static Map<String, Object> serializedList(List<?> list, String name) throws IllegalAccessException, InvocationTargetException,
			NoSuchMethodException {

		if (list.isEmpty() || isWrapperType(list.get(0).getClass())) {
			return simpleMapForValue(list, name);
		}
		return simpleMapForValue(list, name);
	}

	private static boolean isEnum(Object object) {
		return object instanceof Enum;
	}

	private static boolean isList(Object object) {
		return object instanceof List;
	}

	private static Map<String, Object> simpleMapForValue(Object object, String name) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(name, object);
		return map;
	}

	private static final HashSet<Class<?>> WRAPPER_TYPES = getWrapperTypes();

	public static boolean isWrapperType(Type clazz) {
		return WRAPPER_TYPES.contains(clazz);
	}

	private static HashSet<Class<?>> getWrapperTypes() {
		HashSet<Class<?>> ret = new HashSet<Class<?>>();
		ret.add(Boolean.class);
		ret.add(Character.class);
		ret.add(Byte.class);
		ret.add(Short.class);
		ret.add(Integer.class);
		ret.add(Long.class);
		ret.add(Float.class);
		ret.add(Double.class);
		ret.add(Void.class);
		ret.add(String.class);
		ret.add(int.class);
		ret.add(long.class);
		ret.add(double.class);
		ret.add(char.class);
		return ret;
	}
}