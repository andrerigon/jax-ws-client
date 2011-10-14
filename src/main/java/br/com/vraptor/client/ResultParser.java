package br.com.vraptor.client;

import java.lang.reflect.Method;
import java.lang.reflect.Type;

public interface ResultParser {

	<T> T parse(String result, Type resultType);

	Object dealWith(Throwable e, Method method, RestMethod info) throws Throwable;

}
