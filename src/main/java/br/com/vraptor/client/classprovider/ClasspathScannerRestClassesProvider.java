package br.com.vraptor.client.classprovider;

import java.util.LinkedHashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;

public class ClasspathScannerRestClassesProvider implements RestClassesProvider {

	private String packageToScan;

	@Autowired
	public ClasspathScannerRestClassesProvider(String packageToScan) {
		this.packageToScan = packageToScan;
	}

	@Override
	public Set<Class<?>> classes() {
		Set<Class<?>> classes = new LinkedHashSet<Class<?>>();

		for (BeanDefinition component : scanPackage(packageToScan)) {
			classes.add(classFrom(component));
		}
		return classes;
	}

	private Class<?> classFrom(BeanDefinition component) {
		try {
			return Class.forName(component.getBeanClassName());
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	private Set<BeanDefinition> scanPackage(String packageToScan) {
		final ClassPathScanningCandidateComponentProvider provider = new CustomClassPathScanningCandidateComponentProvider(
				false);
		provider.addIncludeFilter(new IsInterfaceTypeFilter());

		Set<BeanDefinition> components = provider.findCandidateComponents(packageToScan);
		return components;
	}

}
