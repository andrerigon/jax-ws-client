package br.com.vraptor.client;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import br.com.caelum.vraptor.Get;
import br.com.caelum.vraptor.Path;
import br.com.caelum.vraptor.Post;

public class RestMethodInfo {

	private String path;
	private List<String> methodNames;
	private HttpMethod httpMethod;

	public RestMethodInfo(Method method) {
		this.path = pathFrom(method);
		this.methodNames = new LinkedList<String>(Arrays.asList(Parameters.namesFor(method)));
		this.httpMethod = HttpMethod.fromMethod(method);
	}

	public String invoke(RestClient restClient, String path, Object[] args) {
		return httpMethod.request(this, path, args, restClient);
	}

	public String getPath() {
		return path;
	}

	public List<String> getMethodNames() {
		return methodNames;
	}

	private static String pathFrom(Method method) {
		if (method.isAnnotationPresent(Path.class)) {
			return method.getAnnotation(Path.class).value()[0];
		}
		if (method.isAnnotationPresent(Get.class)) {
			return method.getAnnotation(Get.class).value()[0];
		}
		if (method.isAnnotationPresent(Post.class)) {
			return method.getAnnotation(Post.class).value()[0];
		}
		return null;
	}
}

enum HttpMethod {
	GET {
		@Override
		public String request(RestMethodInfo info, String path, Object[] args, RestClient restClient) {
			return restClient.get(requestPath(info, path), paramsMap(info, args));
		}

	},

	POST {
		@Override
		public String request(RestMethodInfo info, String path, Object[] args, RestClient restClient) {
			return restClient.post(requestPath(info, path), paramsMap(info, args));
		}
	};

	public static HttpMethod fromMethod(Method method) {
		if (method.isAnnotationPresent(Path.class) || method.isAnnotationPresent(Get.class)) {
			return GET;
		}
		if (method.isAnnotationPresent(Post.class)) {
			return POST;
		}
		throw new IllegalArgumentException("no method found");
	}

	protected Map<String, String> paramsMap(RestMethodInfo info, Object[] args) {
		List<String> names = info.getMethodNames();
		Map<String, String> map = new HashMap<String, String>();
		for (int i = 0; i < names.size(); i++) {
			try {
				map.putAll(Parameters.paramsFor(args[i], names.get(i)));
			} catch (Exception e) {
				throw new IllegalArgumentException("could not obtain params");
			}
		}
		return map;

	}

	protected String requestPath(RestMethodInfo info, String path) {
		return path + info.getPath();
	}

	public abstract String request(RestMethodInfo info, String path, Object args[], RestClient restClient);
}