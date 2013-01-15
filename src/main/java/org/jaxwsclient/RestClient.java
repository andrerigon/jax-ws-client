package org.jaxwsclient;

import java.util.Map;

public interface RestClient {

	String get(String path, Map<String, Object> params, Map<String, String> headers) throws Exception;

	String post(String path, Map<String, Object> map, Map<String, String> headers) throws Exception;

	String put(String path, Map<String, Object> params, Map<String, String> headers) throws Exception;

	String delete(String path, Map<String, Object> params, Map<String, String> headers) throws Exception;

}
