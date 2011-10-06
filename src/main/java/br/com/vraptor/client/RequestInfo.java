package br.com.vraptor.client;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

class RequestInfo {

	private String path;
	private Map<String, Object> params;

	public RequestInfo(String path, List<String> parametersNames, Object[] args) {
		this.params = paramsMap(parametersNames, args);
		this.path = requestPath(path, params);
	}

	public String getPath() {
		return path;
	}

	public Map<String, Object> getParams() {
		return params;
	}

	protected Map<String, Object> paramsMap(List<String> names, Object[] args) {
		Map<String, Object> map = new HashMap<String, Object>();
		for (int i = 0; i < names.size(); i++) {
			try {
				map.putAll(Parameters.paramsFor(args[i], names.get(i)));
			} catch (Exception e) {
				throw new IllegalArgumentException("could not obtain params");
			}
		}
		return map;

	}

	protected String requestPath(String path, Map<String, Object> params) {
		Set<String> pathParams = new LinkedHashSet<String>();

		for (String name : params.keySet()) {
			if (paramExistsInPath(path, name)) {
				Object paramValue = params.get(name);
				path = path.replaceAll(regex(path, name), paramValue == null ?  "" : params.get(name).toString());
				pathParams.add(name);
			}
		}
		removeParams(pathParams, params);
		return path;
	}

	private boolean paramExistsInPath(String path, String name) {
		return isJustKey(path, name) || isKeyValue(path, name);
	}

	private String regex(String path, String name) {
		if (isJustKey(path, name)) {
			return regexKey(name);
		} else if (isKeyValue(path, name)) {
			return regexKeyValue(name);
		} else {
			throw new IllegalStateException();
		}
	}

	private boolean isJustKey(String path, String name) {
		return new Scanner(path).findInLine(regexKey(name)) != null;
	}

	private boolean isKeyValue(String path, String name) {
		return new Scanner(path).findInLine(regexKeyValue(name)) != null;
	}

	private String regexKey(String name) {
		return String.format("\\{%s\\}", name);
	}

	private String regexKeyValue(String name) {
		return "\\{" + name + "\\:?(.*?)[\\}]?\\}";
	}

	private void removeParams(Set<String> pathParams, Map<String, Object> params) {
		for (String param : pathParams) {
			params.remove(param);
		}
	}

}