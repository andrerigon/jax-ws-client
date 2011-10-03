package br.com.vraptor.client;

import java.util.Map;

import org.apache.http.HttpResponse;

public interface RestClient {

	String get(String path, Map<String, Object> params, Map<String, String> headers) throws Exception;

	String get(String path, Map<String, Object> params) throws Exception;

	String post(String path, Map<String, Object> map, Map<String, String> headers) throws Exception;

	String post(String path, Map<String, Object> map) throws Exception;

	HttpResponse postWithHttpResponse(String path, Map<String, String> params, Map<String, String> headers)
			throws Exception;

	String postMultiPartFormData(String path, Map<String, Object> params, Map<String, String> headers) throws Exception;

	String postMultiPartFormData(String path, Map<String, Object> params) throws Exception;

	String put(String path, Map<String, Object> params, Map<String, String> headers) throws Exception;

	String put(String path, Map<String, Object> params) throws Exception;

	String delete(String path, Map<String, Object> params, Map<String, String> headers) throws Exception;

	String delete(String path, Map<String, Object> params) throws Exception;

}
