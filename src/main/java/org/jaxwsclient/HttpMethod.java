package org.jaxwsclient;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;

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
		if (supportsMethod(method, GET.class)) {
			return GET;
		}
		if (supportsMethod(method, POST.class)) {
			return POST;
		}
		if (supportsMethod(method, DELETE.class)) {
			return DELETE;
		}
		if (supportsMethod(method, PUT.class)) {
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