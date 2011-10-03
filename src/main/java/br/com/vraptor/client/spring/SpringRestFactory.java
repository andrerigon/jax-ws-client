package br.com.vraptor.client.spring;

import java.lang.reflect.Proxy;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;

import br.com.caelum.vraptor.ioc.Component;
import br.com.vraptor.client.RestClient;
import br.com.vraptor.client.ResultParser;
import br.com.vraptor.client.classprovider.RestClassesProvider;
import br.com.vraptor.client.handler.RestProxyHandler;

@Component
public class SpringRestFactory implements ApplicationContextAware {

	@Autowired
	RestClient restClient;

	@Autowired
	String path;

	@Autowired
	RestClassesProvider provider;

	@Autowired
	ResultParser parser;

	@Autowired
	public SpringRestFactory(RestClient restClient, String path, RestClassesProvider provider, ResultParser parser) {
		super();
		this.restClient = restClient;
		this.path = path;
		this.provider = provider;
		this.parser = parser;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		for (Class<?> clazz : provider.classes()) {
			addToSpring(applicationContext, proxyFor(clazz));
		}
	}

	private void addToSpring(ApplicationContext applicationContext, Object bean) {
		((ConfigurableApplicationContext) applicationContext).getBeanFactory().registerSingleton(
				bean.getClass().getSimpleName(), bean);
	}

	private Object proxyFor(Class<?> clazz) {
		return Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), new Class<?>[] { clazz },
				new RestProxyHandler(restClient, path, parser));
	}
}
