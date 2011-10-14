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

public class RestMethod {

	private String path;
	private List<String> parametersNames;
	private HttpMethod httpMethod;

	public RestMethod(Method method, String basePath) {
		this.path = buildMethodPath(method, basePath);
		this.parametersNames = new LinkedList<String>(Arrays.asList(Parameters.namesFor(method)));
		this.httpMethod = HttpMethod.fromMethod(method);
	}

	private String buildMethodPath(Method method, String basePath) {
		return removeDoubleSlashes(basePath + "/" + topLevelPath(method) + "/"+  pathFrom(method));
	}

	private String removeDoubleSlashes(String path) {
		return path.replaceAll( "/+" , "/");
	}

	private String topLevelPath(Method method) {
		Class<?> clazz = method.getDeclaringClass();
		if (!clazz.isAnnotationPresent(Path.class)) {
			return "";
		}
		return clazz.getAnnotation(Path.class).value()[0];
	}

	public String invoke(RestClient restClient, Object[] args) throws Exception {
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