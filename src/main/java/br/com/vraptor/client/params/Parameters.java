package br.com.vraptor.client.params;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import com.google.common.collect.ImmutableList;
import com.thoughtworks.paranamer.AnnotationParanamer;
import com.thoughtworks.paranamer.BytecodeReadingParanamer;
import com.thoughtworks.paranamer.CachingParanamer;

public class Parameters {

	private ImmutableList<ParameterInfo> params;
	private List<String> pathParams;
	private String path;
	private ImmutableList<String> names;

	public Parameters(Method m, String path) {
		this.path = path;
		params = paramsInfoFor(m);
		pathParams = pathParams();
		names = paramNames();
	}

	private ImmutableList<String> paramNames() {
		List<String> list = new ArrayList<String>();
		for (ParameterInfo info : params) {
			list.add(info.name());
		}
		return ImmutableList.copyOf(list);
	}

	public boolean isPathParameter(String name) {
		for (String param : pathParams) {
			if (name.startsWith(param)) {
				return true;
			}
		}
		return false;
	}

	public ImmutableList<String> names() {
		return names;
	}

	@Override
	public String toString() {
		return params.toString();
	}

	public int size() {
		return params.size();
	}

	public String name(int position) {
		return params.get(position).name();
	}

	public Set<String> parametersNotPresentInPath(Set<String> params) {
		Set<String> list = new LinkedHashSet<String>();
		for (String name : params) {
			if (!isPathParameter(name)) {
				list.add(name);
			}
		}
		return list;
	}

	private boolean uselessParameter(String name, ParameterInfo parameterInfo) {
		if (!paramExistsInPath(name) && hasLoadAnnotation(parameterInfo) && name.startsWith(parameterInfo.name())) {
			return true;
		}
		return false;
	}

	private boolean hasLoadAnnotation(ParameterInfo parameterInfo) {
		return parameterInfo.hasAnnotation(br.com.caelum.vraptor.util.hibernate.extra.Load.class)
				|| parameterInfo.hasAnnotation(br.com.caelum.vraptor.util.jpa.extra.Load.class);
	}

	private List<String> pathParams() {
		List<String> list = new LinkedList<String>();
		for (ParameterInfo info : params) {
			if (paramExistsInPath(info.name())) {
				list.add(info.name());
			}
		}
		return list;
	}

	private boolean paramExistsInPath(String name) {
		return isJustKey(path, name) || isKeyValue(path, name);
	}

	public static String regex(String path, String name) {
		if (isJustKey(path, name)) {
			return regexKey(name);
		} else if (isKeyValue(path, name)) {
			return regexKeyValue(name);
		} else {
			return "";
		}
	}

	private static boolean isJustKey(String path, String name) {
		return new Scanner(path).findInLine(regexKey(name)) != null;
	}

	private static boolean isKeyValue(String path, String name) {
		return new Scanner(path).findInLine(regexKeyValue(name)) != null;
	}

	private static String regexKey(String name) {
		return String.format("\\{%s\\}", name);
	}

	private static String regexKeyValue(String name) {
		return "\\{" + name + "\\:?(.*?)[\\}]?\\}";
	}

	private static ImmutableList<ParameterInfo> paramsInfoFor(Method m) {
		final CachingParanamer c = new CachingParanamer(new AnnotationParanamer(new BytecodeReadingParanamer()));
		String[] names = c.lookupParameterNames(m);
		Annotation[][] annotations = m.getParameterAnnotations();
		return ImmutableList.copyOf(parameterInfoList(names, annotations));
	}

	private static List<ParameterInfo> parameterInfoList(String[] names, Annotation[][] annotations) {
		List<ParameterInfo> list = new ArrayList<ParameterInfo>(names.length);
		int i = -1;
		while (++i < names.length) {
			list.add(new ParameterInfo(names[i], annotations[i]));
		}
		return list;
	}

	public Set<String> useless(Set<String> names) {
		Set<String> toRemove = new LinkedHashSet<String>();
		for (ParameterInfo info : params) {
			for (String name : names)

				if (uselessParameter(name, info)) {
					toRemove.add(name);
				}
		}
		return toRemove;
	}

}
