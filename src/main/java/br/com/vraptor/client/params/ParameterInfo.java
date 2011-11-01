package br.com.vraptor.client.params;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.List;

public final class ParameterInfo {

	private String name;

	private List<Annotation> annotations;

	public ParameterInfo(String name, Annotation... annotations) {
		this.name = name;
		this.annotations = Arrays.asList(annotations);
	}

	public String name() {
		return name;
	}

	public boolean hasAnnotation(Class<? extends Annotation> ann) {
		for (Annotation a : annotations) {
			if (ann.isAssignableFrom(a.getClass())) {
				return true;
			}
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	public <T> T annotation(Class<T> annotation) {
		for (Annotation a : annotations) {
			if (annotation.isAssignableFrom(a.getClass())) {
				return (T) a;
			}
		}
		return null;
	}

	@Override
	public String toString() {
		return String.format("[Parameter] name: <%s> annotations: [%s]", name, annotationsToString());
	}

	private String annotationsToString() {
		final StringBuilder builder = new StringBuilder();
		for (Annotation a : annotations) {
			builder.append(a + ", ");
		}

		builder.delete(builder.length() - 2, builder.length());
		return builder.toString();
	}

}
