package org.jaxwsclient;

import static org.jaxwsclient.test.util.CustomMatchers.aMapWithKeyValue;
import static org.mockito.Matchers.anyMap;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

import org.jaxwsclient.RestClient;
import org.jaxwsclient.ResultParser;
import org.jaxwsclient.handler.RestProxyHandler;
import org.jaxwsclient.test.data.SampleService;
import org.jaxwsclient.test.data.WithPathSampleService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
@SuppressWarnings("unchecked")
public class RequestTest {

	@Mock
	RestClient client;

	String path = "/jedi/";

	@Mock
	ResultParser resultParser;

	@Test
	public void should_extract_correct_info_and_do_a_get() throws Throwable {

		sampleServiceFor(SampleService.class).testGet("andre");

		verify(client).get(eq(path + "testGet"), aMapWithKeyValue("name", "andre"), anyMap());

	}

	@Test
	public void should_extract_correct_info_using_delete_and_do_a_delete() throws Throwable {

		sampleServiceFor(SampleService.class).testDelete(12);

		verify(client).delete(eq(path + "testDelete"), aMapWithKeyValue("id", 12), anyMap());

	}

	@Test
	public void should_extract_correct_info_and_make_a_request_using_path_params() throws Throwable {

		sampleServiceFor(SampleService.class).testPathWithParam("12", "andre");

		verify(client).get(eq(path + "bla/12/andre"), anyMap(), anyMap());

	}

	@Test
	public void should_extract_correct_info_and_do_a_put() throws Throwable {

		sampleServiceFor(SampleService.class).testPut(12);

		verify(client).put(eq(path + "testPut"), aMapWithKeyValue("id", 12), anyMap());

	}

	@Test
	public void should_build_path_with_regex() throws Throwable {

		sampleServiceFor(SampleService.class).testWithRegex(12);

		verify(client).get(eq(path + "regex/12/bla"), anyMap(), anyMap());

	}

	@Test
	public void should_build_path_with_regex_complex() throws Throwable {

		sampleServiceFor(SampleService.class).testWithRegexComplex(314159265, 666, 271828183, "haihai");

		verify(client).get(eq(path + "regex-complex/314159265/ALL/666/GONE/271828183/TO/haihai/HELL"), anyMap(), anyMap());

	}

	@Test
	public void should_extract_correct_info_when_path_annotation_in_class_level() throws Throwable {

		sampleServiceFor(WithPathSampleService.class).testGet("");

		verify(client).get(eq(path + "prefix/testGet"), anyMap(), anyMap());
	}

	@Test
	public void should_encode_uri() throws Exception {

		sampleServiceFor(SampleService.class).testPathWithParam("12", "and&re");

		verify(client).get(eq(path + "bla/12/and%26re"), anyMap(), anyMap());
	}

	@Test
	public void should_build_path_with_regex_and_null_parameter() throws Throwable {

		sampleServiceFor(SampleService.class).testWithRegex(null);

		verify(client).get(eq(path + "regex/bla"), anyMap(), anyMap());

	}

	private <T> T sampleServiceFor(Class<T> clazz) {
		return (T) Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), new Class[] { clazz },
				(InvocationHandler) new RestProxyHandler(client, path, resultParser, clazz));
	}
}
