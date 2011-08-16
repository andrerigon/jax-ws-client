package br.com.vraptor.client;

public interface ResultParser {

	<T> T parse(String result, Class<?> resultType);

}
