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

	private final String path;

	private Map<Method, RestMethodInfo> cache = new HashMap<Method, RestMethodInfo>();

	private final ResultParser parser;

	public RestProxyHandler(RestClient restClient, String path, ResultParser parser) {
		this.restClient = restClient;
		this.path = path;
		this.parser = parser;
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		RestMethodInfo info = findMethodInfo(method);
		final String result = info.invoke(restClient, path, args);
		return parser.parse(result, method.getReturnType());
	}

	private RestMethodInfo findMethodInfo(Method method) {
		if (!cache.containsKey(method)) {
			cache.put(method, new RestMethodInfo(method));
		}
		return cache.get(method);
	}
}
