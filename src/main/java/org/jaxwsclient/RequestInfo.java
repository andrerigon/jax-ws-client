package org.jaxwsclient;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.jaxwsclient.params.Body;
import org.jaxwsclient.params.Parameters;
import org.jaxwsclient.params.ParametersSerializer;

import com.google.gson.Gson;

class RequestInfo {

	private String path;
	private Map<String, Object> params;
	private boolean body = false;
	private String bodyContentType;

	public RequestInfo(String path, Parameters parameters, Object[] args) throws UnsupportedEncodingException {
		this.params = paramsMap(parameters, args);
		this.path = UriUtils.removeDoubleSlashes(requestPath(path, params, parameters));
	}

	public String getPath() {
		return path;
	}

	public Map<String, Object> getParams() {
		return params;
	}

	public boolean hasBody() {
		return this.body;
	}
	

	protected Map<String, Object> paramsMap(Parameters parameters, final Object[] args) {
		Map<String, Object> map = new HashMap<String, Object>();
		for (int i = 0; i < parameters.size(); i++) {
			try {
				final Object arg = args[i];
				if (parameters.getInfo(i).hasAnnotation(Body.class)) {
					this.body = true;
					this.bodyContentType = parameters.getInfo(i).annotation(Body.class).contentType();
					map.put("body", arg == null ? null : new Gson().toJson(arg));
				}
				if (args[i] != null) {
					map.putAll(ParametersSerializer.paramsFor(arg, parameters.name(i)));
				}
			} catch (Exception e) {
				throw new IllegalArgumentException("could not obtain params");
			}
		}
		return map;

	}

	protected String requestPath(String path, Map<String, Object> params, Parameters parameters) throws UnsupportedEncodingException {

		Set<String> pathParams = new LinkedHashSet<String>();

		for (String name : params.keySet()) {
			if (parameters.isPathParameter(name)) {
				Object paramValue = params.get(name);
				path = path.replaceAll(Parameters.regex(path, name), paramValue == null ? "" : encode(params.get(name).toString()));
				pathParams.add(name);
			}
		}
		removeParams(pathParams, params);
		return removeNullPathParameters(path, parameters);
	}

	private String removeNullPathParameters(String path, Parameters parameters) {
		for (String name : parameters.names()) {
			if (parameters.isPathParameter(name)) {
				path = path.replaceAll(Parameters.regex(path, name), "");
			}
		}
		return path;
	}

	private String encode(String string) throws UnsupportedEncodingException {
		return URLEncoder.encode(string, "utf-8");
	}

	private void removeParams(Set<String> pathParams, Map<String, Object> params) {
		for (String param : pathParams) {
			params.remove(param);
		}
	}

	public String getBodyContentType() {
		return bodyContentType;
	}

}