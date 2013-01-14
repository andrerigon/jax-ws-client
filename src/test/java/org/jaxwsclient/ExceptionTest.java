package org.jaxwsclient;

import static org.jaxwsclient.test.data.Samples.sampleGetMethod;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyMap;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

import java.lang.reflect.Proxy;

import org.jaxwsclient.RestClient;
import org.jaxwsclient.RestMethod;
import org.jaxwsclient.ResultParser;
import org.jaxwsclient.handler.RestProxyHandler;
import org.jaxwsclient.test.data.CheckedException;
import org.jaxwsclient.test.data.SampleService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class ExceptionTest {

	@Mock
	ResultParser resultParser;

	@Mock
	RestClient client;

	@Test
	public void should_deal_with_exception() throws Throwable {
		RuntimeException ex = new RuntimeException();
		throw_inside_client(ex);

		sampleService().testGet("andre");

		verify(resultParser).dealWith(eq(ex), eq(sampleGetMethod()), any(RestMethod.class));
	}

	@Test(expected = IllegalArgumentException.class)
	public void should_propagate_correct_runtime_exception() throws Throwable {

		IllegalArgumentException ex = new IllegalArgumentException();

		throw_inside_client(ex);

		throw_inside_result_parser(ex);

		sampleService().testGet("andre");
	}
	
	@Test(expected = CheckedException.class)
	public void should_propagate_correct_checked_exception() throws Throwable {

		CheckedException ex = new CheckedException();

		throw_inside_client(ex);

		throw_inside_result_parser(ex);

		sampleService().testGet("andre");
	}

	private void throw_inside_result_parser(Exception ex) throws Throwable, NoSuchMethodException {
		doThrow(ex).when(resultParser).dealWith(eq(ex), eq(sampleGetMethod()), any(RestMethod.class));
	}

	@SuppressWarnings("unchecked")
	private void throw_inside_client(Throwable t) throws Exception {
		doThrow(t).when(client).get(anyString(), anyMap());
	}

	private SampleService sampleService() {
		return (SampleService) Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),
				new Class[] { SampleService.class }, new RestProxyHandler(client, "", resultParser, SampleService.class));
	}

}
