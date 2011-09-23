package br.com.vraptor.client.handler;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import br.com.vraptor.client.RestClient;
import br.com.vraptor.client.RestMethodInfo;
import br.com.vraptor.client.ResultParser;

public class RestProxyHandler implements InvocationHandler {

	private final RestClient restClient;

	private final String basePath;

	private Map<Method, RestMethodInfo> cache = new HashMap<Method, RestMethodInfo>();

	private final ResultParser parser;

	public RestProxyHandler(RestClient restClient, String basePath, ResultParser parser) {
		this.restClient = restClient;
		this.basePath = basePath;
		this.parser = parser;
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		if (equalsMethod(method)) {
			return false;
		}
		if (toString(method)) {
			return "[vraptor-client] proxy for " + interfaceName(proxy);
		}
		RestMethodInfo info = findMethodInfo(method);
		try {
			final String result = info.invoke(restClient, args);
			return parser.parse(result, method.getGenericReturnType());
		} catch (Throwable e) {
			return parser.dealWith(e, method, info);
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

	private RestMethodInfo findMethodInfo(Method method) {
		if (!cache.containsKey(method)) {
			cache.put(method, new RestMethodInfo(method, basePath));
		}
		return cache.get(method);
	}
}
