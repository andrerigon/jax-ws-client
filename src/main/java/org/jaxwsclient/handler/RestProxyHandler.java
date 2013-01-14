package org.jaxwsclient.handler;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

import org.jaxwsclient.RestClient;
import org.jaxwsclient.RestMethod;
import org.jaxwsclient.ResultParser;

import com.google.common.collect.ImmutableMap;

public class RestProxyHandler implements InvocationHandler {

	private final RestClient restClient;

	private final ImmutableMap<Method, RestMethod> cache;

	private final ResultParser parser;

	@SuppressWarnings("unchecked")
	public static <T> T newProxy(RestClient restClient, String basePath, ResultParser parser, Class<T> clazz) {
		return (T) Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), new Class<?>[] { clazz }, new RestProxyHandler(restClient,
				basePath, parser, clazz));
	}

	public RestProxyHandler(RestClient restClient, String basePath, ResultParser parser, Class<?> clazz) {
		this.restClient = restClient;
		this.parser = parser;

		this.cache = readMethods(basePath, clazz);
	}

	private ImmutableMap<Method, RestMethod> readMethods(String basePath, Class<?> clazz) {
		Map<Method, RestMethod> map = new HashMap<Method, RestMethod>();
		for (Method m : clazz.getDeclaredMethods()) {
			map.put(m, new RestMethod(m, basePath));
		}
		return ImmutableMap.copyOf(map);
	}

	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		if (equalsMethod(method)) {
			return false;
		}
		if (toString(method)) {
			return "[vraptor-client] proxy for " + interfaceName(proxy);
		}
		RestMethod restMethod = cache.get(method);
		try {
			final String result = restMethod.invoke(restClient, args);
			return parser.parse(result, method.getGenericReturnType());
		} catch (Throwable e) {
			return parser.dealWith(e, method, restMethod);
		}
	}

	private String interfaceName(Object proxy) {
		return proxy.getClass().getInterfaces()[0].getSimpleName();
	}

	private boolean toString(Method method) {
		return method.getName().equals("toString");
	}

	private boolean equalsMethod(Method method) {
		return method.getName().equals("equals");
	}
}
