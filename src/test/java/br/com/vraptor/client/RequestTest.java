package br.com.vraptor.client;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyMap;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static br.com.vraptor.client.test.data.Samples.*;
import static br.com.vraptor.client.test.util.CustomMatchers.*;

import java.lang.reflect.Method;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import br.com.vraptor.client.handler.RestProxyHandler;

@RunWith(MockitoJUnitRunner.class)
public class RequestTest {

	@Mock
	RestClient client;

	String path = "/jedi/";

	@Mock
	ResultParser resultParser;

	@Test
	public void should_extract_correct_info_and_do_a_get() throws Throwable {

		restProxyHandler().invoke(null, sampleGetMethod(), new Object[] { "andre" });

		verify(client).get(eq(path + "testGet"), aMapWithKeyValue("name", "andre"));

	}

	@Test
	public void should_extract_correct_info_using_path_and_do_a_get() throws Throwable {

		restProxyHandler().invoke(null, samplePathMethod(), new Object[] { "andre" });

		verify(client).get(eq(path + "testPath"), aMapWithKeyValue("name", "andre"));

	}

	@Test
	public void should_extract_correct_info_using_delete_and_do_a_delete() throws Throwable {

		restProxyHandler().invoke(null, sampleDeleteMethod(), new Object[] { 12 });

		verify(client).delete(eq(path + "testDelete"), aMapWithKeyValue("id", 12));

	}

	@Test
	@SuppressWarnings("unchecked")
	public void should_extract_correct_info_and_make_a_request_using_path_params() throws Throwable {

		restProxyHandler().invoke(null, sampleMethodWithParamInThePath(), new Object[] { 12, "andre" });

		verify(client).get(eq(path + "bla/12/andre"), anyMap());

	}

	@Test
	public void should_extract_correct_info_and_do_a_put() throws Throwable {

		restProxyHandler().invoke(null, samplePutMethod(), new Object[] { 12 });

		verify(client).put(eq(path + "testPut"), aMapWithKeyValue("id", 12));

	}

	@Test
	@SuppressWarnings("unchecked")
	public void should_deal_with_exceptions() throws Throwable {

		when(client.get(anyString(), anyMap())).thenThrow(new RuntimeException());

		Method get = sampleGetMethod();

		restProxyHandler().invoke(null, get, new Object[] { 12 });

		verify(resultParser).dealWith(any(RuntimeException.class), eq(get), (RestMethodInfo) anyObject());

	}

	@Test
	@SuppressWarnings("unchecked")
	public void should_build_path_with_regex() throws Throwable {

		restProxyHandler().invoke(null, sampleMethodWithRegex(), new Object[] { 12 });

		verify(client).get(eq(path + "regex/12/bla"), anyMap());

	}

	@Test
	@SuppressWarnings("unchecked")
	public void should_build_path_with_regex_complex() throws Throwable {

		restProxyHandler().invoke(null, sampleMethodWithRegexComplex(),
				new Object[] { 314159265, 666, 271828183, "haihai" });

		verify(client).get(eq(path + "regex-complex/314159265/ALL/666/GONE/271828183/TO/haihai/HELL"), anyMap());

	}
	
	@Test
	@SuppressWarnings("unchecked")
	public void should_extract_correct_info_when_path_annotation_in_class_level() throws Throwable{
		restProxyHandler().invoke(null, sampleMethodWithPathInClassLevel(),
				new Object[] { "palmeiras" });
		
		verify( client ).get( eq(path + "prefix/testGet") , anyMap());
	}

	private RestProxyHandler restProxyHandler() {
		return new RestProxyHandler(client, path, resultParser);
	}
}
