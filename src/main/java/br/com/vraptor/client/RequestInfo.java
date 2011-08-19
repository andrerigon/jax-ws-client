package br.com.vraptor.client;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

class RequestInfo {

	private String path;
	private Map<String, String> params;

	public RequestInfo(String path, List<String> parametersNames, Object[] args) {

		this.params = paramsMap(parametersNames, args);
		this.path = requestPath(path, params);
	}

	public String getPath() {
		return path;
	}

	public Map<String, String> getParams() {
		return params;
	}

	protected Map<String, String> paramsMap(List<String> names, Object[] args) {
		Map<String, String> map = new HashMap<String, String>();
		for (int i = 0; i < names.size(); i++) {
			try {
				map.putAll(Parameters.paramsFor(args[i], names.get(i)));
			} catch (Exception e) {
				throw new IllegalArgumentException("could not obtain params");
			}
		}
		return map;

	}

	protected String requestPath(String path, Map<String, String> params) {
		Set<String> pathParams = new LinkedHashSet<String>();
		for (String name : params.keySet()) {
			String pathParam = "{" + name + "}";
			if (path.contains(pathParam)) {
				path = path.replace(pathParam, params.get(name));
				pathParams.add(name);
			}
		}
		removePathParams(pathParams, params);
		return path;
	}

	private void removePathParams(Set<String> pathParams, Map<String, String> params) {
		for (String param : pathParams) {
			params.remove(param);
		}
	}
}