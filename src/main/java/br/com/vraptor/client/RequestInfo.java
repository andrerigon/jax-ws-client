package br.com.vraptor.client;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import br.com.vraptor.client.params.ParameterInfo;
import br.com.vraptor.client.params.Parameters;

class RequestInfo {

	private String path;
	private Map<String, Object> params;

	public RequestInfo(String path, List<ParameterInfo> parametersInfo, Object[] args)
			throws UnsupportedEncodingException {
		this.params = paramsMap(parametersInfo, args);
		this.path = UriUtils.removeDoubleSlashes(requestPath(path, params, parametersInfo));
	}

	public String getPath() {
		return path;
	}

	public Map<String, Object> getParams() {
		return params;
	}

	protected Map<String, Object> paramsMap(List<ParameterInfo> names, Object[] args) {
		Map<String, Object> map = new HashMap<String, Object>();
		for (int i = 0; i < names.size(); i++) {
			try {
				if (args[i] != null) {
					map.putAll(Parameters.paramsFor(args[i], names.get(i).name()));
				}
			} catch (Exception e) {
				throw new IllegalArgumentException("could not obtain params");
			}
		}
		return map;

	}

	protected String requestPath(String path, Map<String, Object> params, List<ParameterInfo> parametersInfo)
			throws UnsupportedEncodingException {
		removeParamsWithLoadAnnotation(path, params, parametersInfo);

		Set<String> pathParams = new LinkedHashSet<String>();

		for (ParameterInfo info : parametersInfo) {
			final String name = info.name();
			if (paramExistsInPath(path, name)) {
				Object paramValue = params.get(name);
				path = path
						.replaceAll(regex(path, name), paramValue == null ? "" : encode(params.get(name).toString()));
				pathParams.add(name);
			}
		}
		removeParams(pathParams, params);
		return path;
	}

	private String encode(String string) throws UnsupportedEncodingException {
		return URLEncoder.encode(string, "utf-8");
	}

	private void removeParamsWithLoadAnnotation(String path, Map<String, Object> params,
			List<ParameterInfo> parametersInfo) {
		Set<String> toRemove = new LinkedHashSet<String>();
		Set<String> paramsNames = parametersNotPresentInPath(path, params.keySet());
		for (ParameterInfo parameterInfo : parametersInfo) {
			for (String name : paramsNames) {
				if (uselessParameter(name, parameterInfo)) {
					toRemove.add(name);
				}
			}
		}
		removeParams(toRemove, params);
	}

	private Set<String> parametersNotPresentInPath(String path, Set<String> params) {
		Set<String> list = new LinkedHashSet<String>();
		for (String name : params) {
			if (!paramExistsInPath(path, name)) {
				list.add(name);
			}
		}
		return list;
	}

	private boolean uselessParameter(String name, ParameterInfo parameterInfo) {
		if (hasLoadAnnotation(parameterInfo) && name.startsWith(parameterInfo.name())) {
			return true;
		}
		return false;
	}

	private boolean hasLoadAnnotation(ParameterInfo parameterInfo) {
		return parameterInfo.hasAnnotation(br.com.caelum.vraptor.util.hibernate.extra.Load.class)
				|| parameterInfo.hasAnnotation(br.com.caelum.vraptor.util.jpa.extra.Load.class);
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