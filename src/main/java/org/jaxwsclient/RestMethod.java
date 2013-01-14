package org.jaxwsclient;

import java.lang.reflect.Method;

import javax.ws.rs.Path;

import org.jaxwsclient.params.Parameters;


public class RestMethod {

	private String path;
	private Parameters parameters;
	private HttpMethod httpMethod;

	public RestMethod(Method method, String basePath) {
		this.path = buildMethodPath(method, basePath);
		this.parameters = new Parameters(method, path);
		this.httpMethod = HttpMethod.fromMethod(method);
	}

	private String buildMethodPath(Method method, String basePath) {
		return UriUtils.removeDoubleSlashes(basePath + "/" + topLevelPath(method) + "/" + pathFrom(method));
	}

	private String topLevelPath(Method method) {
		Class<?> clazz = method.getDeclaringClass();
		if (!clazz.isAnnotationPresent(Path.class)) {
			return "";
		}
		return clazz.getAnnotation(Path.class).value();
	}

	public String invoke(RestClient restClient, Object[] args) throws Exception {
		return httpMethod.request(new RequestInfo(path, parameters, args), restClient);
	}

	private static String pathFrom(Method method) {
		if (method.isAnnotationPresent(Path.class)) {
			return method.getAnnotation(Path.class).value();
		}
		return "";
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("RestMethodInfo [path=").append(path).append(", parametersNames=").append(parameters).append(", httpMethod=")
				.append(httpMethod).append("]");
		return builder.toString();
	}

	public String getPath() {
		return path;
	}

	public String getHttpMethod() {
		return httpMethod.toString();
	}

}