package br.com.vraptor.client;

import java.lang.reflect.Method;

import br.com.caelum.vraptor.Delete;
import br.com.caelum.vraptor.Get;
import br.com.caelum.vraptor.Path;
import br.com.caelum.vraptor.Post;

enum HttpMethod {
	GET {
		@Override
		public String makeRequest(RequestInfo requestInfo, RestClient restClient) {
			return restClient.get(requestInfo.getPath(), requestInfo.getParams());
		}

	},

	POST {
		@Override
		public String makeRequest(RequestInfo requestInfo, RestClient restClient) {
			return restClient.post(requestInfo.getPath(), requestInfo.getParams());
		}
	},
	DELETE {

		@Override
		public String makeRequest(RequestInfo requestInfo, RestClient restClient) {
			return restClient.delete(requestInfo.getPath(), requestInfo.getParams());
		}
	};

	public static HttpMethod fromMethod(Method method) {
		if (method.isAnnotationPresent(Path.class) || method.isAnnotationPresent(Get.class)) {
			return GET;
		}
		if (method.isAnnotationPresent(Post.class)) {
			return POST;
		}
		if (method.isAnnotationPresent(Delete.class)) {
			return DELETE;
		}
		throw new IllegalArgumentException("no method found");
	}

	public String request(RequestInfo info, RestClient restClient) {
		return makeRequest(info, restClient);
	}

	protected abstract String makeRequest(RequestInfo requestInfo, RestClient restClient);
}