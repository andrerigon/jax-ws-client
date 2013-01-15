package org.jaxwsclient.handler;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

import org.jaxwsclient.RestClient;
import org.jaxwsclient.RestMethod;
import org.jaxwsclient.ResultParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableMap;

public class RestProxyHandler implements InvocationHandler {

	private final RestClient restClient;

	private final ImmutableMap<Method, RestMethod> cache;

	private final ResultParser parser;

	private final Logger logger;

	@SuppressWarnings("unchecked")
	public static <T> T newProxy(RestClient restClient, String basePath, ResultParser parser, Class<T> clazz, Logger logger) {
		return (T) Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), new Class<?>[] { clazz }, new RestProxyHandler(restClient,
				basePath, parser, clazz, logger));
	}

	@SuppressWarnings("unchecked")
	public static <T> T newProxy(RestClient restClient, String basePath, ResultParser parser, Class<T> clazz) {
		return (T) Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), new Class<?>[] { clazz }, new RestProxyHandler(restClient,
				basePath, parser, clazz, LoggerFactory.getLogger(clazz)));
	}

	public RestProxyHandler(RestClient restClient, String basePath, ResultParser parser, Class<?> clazz) {
		this.restClient = restClient;
		this.parser = parser;
		this.logger = LoggerFactory.getLogger(clazz);
		this.cache = readMethods(basePath, clazz);
	}

	public RestProxyHandler(RestClient restClient, String basePath, ResultParser parser, Class<?> clazz, Logger logger) {
		this.restClient = restClient;
		this.parser = parser;
		this.logger = logger;
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
			long time = System.currentTimeMillis();
			final String httpResult = restMethod.invoke(restClient, args);
			if (logger.isDebugEnabled()) {
				time = System.currentTimeMillis() - time;
				logger.debug(String.format("Method %s executed in %d ms,", restMethod.toString(), time));
			}
			if (logger.isDebugEnabled()) {
				time = System.currentTimeMillis();
			}

			Object result = parser.parse(httpResult, method.getGenericReturnType());
			if (logger.isDebugEnabled()) {
				time = System.currentTimeMillis() - time;
				logger.debug(String.format("%s parsed in %d ms", result.getClass().getName(), time));
			}
			return parser;
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
