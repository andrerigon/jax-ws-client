package br.com.vraptor.client;

import java.util.Map;

import org.apache.http.HttpResponse;

public interface RestClient {

	String get(String path, Map<String, String> params, Map<String, String> headers);

	String get(String path, Map<String, String> params);

	String post(String path, Map<String, String> map, Map<String, String> headers);

	String post(String path, Map<String, String> map);

	HttpResponse postWithHttpResponse(String path, Map<String, String> params, Map<String, String> headers);

	String postMultiPartFormData(String path, Map<String, Object> params, Map<String, String> headers);

	String postMultiPartFormData(String path, Map<String, Object> params);

	String put(String path, Map<String, String> params, Map<String, String> headers);

	String put(String path, Map<String, String> params);

	String delete(String path, Map<String, String> params, Map<String, String> headers);

	String delete(String path, Map<String, String> params);

}
