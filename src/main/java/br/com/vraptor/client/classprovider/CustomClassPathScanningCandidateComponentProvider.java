package br.com.vraptor.client.classprovider;

import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;

public class CustomClassPathScanningCandidateComponentProvider extends ClassPathScanningCandidateComponentProvider {

	public CustomClassPathScanningCandidateComponentProvider(boolean useDefaultFilters) {
		super(useDefaultFilters);
	}

	@Override
	protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
		return true;
	}

}
