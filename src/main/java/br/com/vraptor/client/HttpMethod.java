package br.com.vraptor.client;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import br.com.caelum.vraptor.Delete;
import br.com.caelum.vraptor.Get;
import br.com.caelum.vraptor.Path;
import br.com.caelum.vraptor.Post;
import br.com.caelum.vraptor.Put;

enum HttpMethod {
	GET {
		@Override
		public String makeRequest(RequestInfo requestInfo, RestClient restClient) throws Exception {
			return restClient.get(requestInfo.getPath(), requestInfo.getParams());
		}

	},

	POST {
		@Override
		public String makeRequest(RequestInfo requestInfo, RestClient restClient) throws Exception {
			return restClient.post(requestInfo.getPath(), requestInfo.getParams());
		}

	},
	DELETE {

		@Override
		public String makeRequest(RequestInfo requestInfo, RestClient restClient) throws Exception {
			return restClient.delete(requestInfo.getPath(), requestInfo.getParams());
		}
	},
	PUT {

		@Override
		protected String makeRequest(RequestInfo requestInfo, RestClient restClient) throws Exception {
			return restClient.put(requestInfo.getPath(), requestInfo.getParams());
		}
	};

	@SuppressWarnings("unchecked")
	public static HttpMethod fromMethod(Method method) {
		if (supportsMethod(method, Path.class, Get.class)) {
			return GET;
		}
		if (supportsMethod(method, Post.class)) {
			return POST;
		}
		if (supportsMethod(method, Delete.class)) {
			return DELETE;
		}
		if (supportsMethod(method, Put.class)) {
			return PUT;
		}
		throw new IllegalArgumentException("no method found");
	}

	private static boolean supportsMethod(Method m, Class<? extends Annotation>... array) {
		for (Class<? extends Annotation> a : array) {
			if (m.isAnnotationPresent(a)) {
				return true;
			}
		}
		return false;
	}

	public String request(RequestInfo info, RestClient restClient) throws Exception {
		return makeRequest(info, restClient);
	}

	protected abstract String makeRequest(RequestInfo requestInfo, RestClient restClient) throws Exception;
}