package br.com.vraptor.client;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import net.vidageek.mirror.dsl.Mirror;
import net.vidageek.mirror.list.dsl.Matcher;

import com.thoughtworks.paranamer.AnnotationParanamer;
import com.thoughtworks.paranamer.BytecodeReadingParanamer;
import com.thoughtworks.paranamer.CachingParanamer;

public class Parameters {

	private static final Mirror MIRROR = new Mirror();
	private static final Matcher<Field> ONLY_INSTANCE_FIELDS_MAPPER = new Matcher<Field>() {

		@Override
		public boolean accepts(Field field) {
			return !Modifier.isStatic(field.getModifiers());
		}

	};

	public static Map<String, Object> paramsFor(Object object, String name)
			throws IllegalAccessException, InvocationTargetException,
			NoSuchMethodException {
		if (isWrapperType(object.getClass()) || isList(object)
				|| isEnum(object)) {
			return simpleMapForValue(object, name);
		}
		Map<String, Object> params = new HashMap<String, Object>();
		for (Field f : fieldsFrom(object)) {
			String paramName = paramName(name, f);
			Object fieldValue = fieldValue(object, f);
			params.putAll(mapForField(paramName, fieldValue));
		}
		return params;
	}

	private static Map<String, Object> mapForField(String name, Object value)
			throws IllegalAccessException, InvocationTargetException,
			NoSuchMethodException {
		if (value == null) {
			return Collections.emptyMap();
		}
		if (isWrapperType(value.getClass())) {
			return simpleMapForValue(value, name);
		}
		return paramsFor(value, name);
	}

	private static boolean isEnum(Object object) {
		return object.getClass().isEnum();
	}

	private static boolean isList(Object object) {
		return object instanceof List;
	}

	private static Map<String, Object> simpleMapForValue(Object object,
			String name) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(name, object);
		return map;
	}

	private static String paramName(String name, Field f) {
		return String.format("%s.%s", name, f.getName());
	}

	private static Object fieldValue(Object object, Field f) {
		return MIRROR.on(object).get().field(f.getName());
	}

	private static List<Field> fieldsFrom(Object object) {
		final List<Field> fields = MIRROR.on(object.getClass()).reflectAll()
				.fields().matching(ONLY_INSTANCE_FIELDS_MAPPER);
		return fields;
	}

	private static final HashSet<Class<?>> WRAPPER_TYPES = getWrapperTypes();

	public static boolean isWrapperType(Class<?> clazz) {
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

	public static String[] namesFor(Method m) {
		final CachingParanamer c = new CachingParanamer(
				new AnnotationParanamer(new BytecodeReadingParanamer()));
		return c.lookupParameterNames(m);
	}
}