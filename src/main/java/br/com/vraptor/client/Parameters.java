package br.com.vraptor.client;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.beanutils.BeanUtils;

import com.thoughtworks.paranamer.AnnotationParanamer;
import com.thoughtworks.paranamer.BytecodeReadingParanamer;
import com.thoughtworks.paranamer.CachingParanamer;

public class Parameters {

	public static Map<String, String> paramsFor(Object object, String name) throws IllegalAccessException,
			InvocationTargetException, NoSuchMethodException {
		if (isWrapperType(object.getClass())) {
			Map<String, String> map = new HashMap<String, String>();
			map.put(name, object.toString());
			return map;
		}
		final Map<String, String> params = new HashMap<String, String>();
		@SuppressWarnings("unchecked")
		final Map<String, String> beanMap = (Map<String, String>) BeanUtils.describe(object);
		beanMap.remove("class");
		for (Entry<String, String> entry : beanMap.entrySet()) {
			params.put(String.format("%s.%s", name, entry.getKey()), entry.getValue());
		}
		return params;
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
		return ret;
	}

	public static String[] namesFor(Method m) {
		final CachingParanamer c = new CachingParanamer(new AnnotationParanamer(new BytecodeReadingParanamer()));
		return c.lookupParameterNames(m);
	}
}