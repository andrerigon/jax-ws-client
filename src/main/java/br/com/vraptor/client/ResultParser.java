package br.com.vraptor.client;

import java.lang.reflect.Method;

public interface ResultParser {

	<T> T parse(String result, Class<T> resultType);
	
	Object dealWith(Throwable e, Method method);

}
