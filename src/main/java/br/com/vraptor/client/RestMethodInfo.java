package br.com.vraptor.client;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import br.com.caelum.vraptor.Delete;
import br.com.caelum.vraptor.Get;
import br.com.caelum.vraptor.Path;
import br.com.caelum.vraptor.Post;
import br.com.caelum.vraptor.Put;

public class RestMethodInfo {

	private String path;
	private List<String> parametersNames;
	private HttpMethod httpMethod;

	public RestMethodInfo(Method method, String basePath) {
		this.path = basePath + pathFrom(method);
		this.parametersNames = new LinkedList<String>(Arrays.asList(Parameters.namesFor(method)));
		this.httpMethod = HttpMethod.fromMethod(method);
	}

	public String invoke(RestClient restClient, Object[] args) {
		return httpMethod.request(new RequestInfo(path, parametersNames, args), restClient);
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
		if (method.isAnnotationPresent(Delete.class)) {
			return method.getAnnotation(Delete.class).value()[0];
		}
		if (method.isAnnotationPresent(Put.class)) {
			return method.getAnnotation(Put.class).value()[0];
		}
		throw new IllegalArgumentException("no path information found for method: " + method.getName());
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("RestMethodInfo [path=").append(path).append(", parametersNames=").append(parametersNames)
				.append(", httpMethod=").append(httpMethod).append("]");
		return builder.toString();
	}

	public String getPath() {
		return path;
	}

	public List<String> getParametersNames() {
		return parametersNames;
	}

	public String getHttpMethod() {
		return httpMethod.toString();
	}

}