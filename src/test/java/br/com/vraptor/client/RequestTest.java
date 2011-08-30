package br.com.vraptor.client;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyMap;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.Method;
import java.util.Map;

import javax.inject.Named;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import br.com.caelum.vraptor.Delete;
import br.com.caelum.vraptor.Get;
import br.com.caelum.vraptor.Path;
import br.com.caelum.vraptor.Put;
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

	private Method sampleMethodWithRegex() throws SecurityException, NoSuchMethodException {
		return SampleService.class.getDeclaredMethod("testWithRegex", int.class);
	}

	private Method sampleMethodWithRegexComplex() throws SecurityException, NoSuchMethodException {
		return SampleService.class.getDeclaredMethod("testWithRegexComplex", int.class, int.class, int.class,
				String.class);
	}

	private Method samplePutMethod() throws SecurityException, NoSuchMethodException {
		return SampleService.class.getDeclaredMethod("testPut", int.class);
	}

	private Method sampleMethodWithParamInThePath() throws SecurityException, NoSuchMethodException {
		return SampleService.class.getDeclaredMethod("testPathWithParam", String.class, String.class);
	}

	private Method sampleDeleteMethod() throws SecurityException, NoSuchMethodException {
		return SampleService.class.getDeclaredMethod("testDelete", int.class);
	}

	private Method samplePathMethod() throws SecurityException, NoSuchMethodException {
		return SampleService.class.getDeclaredMethod("testPath", String.class);
	}

	private RestProxyHandler restProxyHandler() {
		return new RestProxyHandler(client, path, resultParser);
	}

	private Method sampleGetMethod() throws SecurityException, NoSuchMethodException {
		return SampleService.class.getDeclaredMethod("testGet", String.class);
	}

	private Map<String, String> aMapWithKeyValue(final String key, final Object value) {
		return Matchers.argThat(new BaseMatcher<Map<String, String>>() {

			@Override
			public boolean matches(Object item) {
				@SuppressWarnings("unchecked")
				Map<String, String> map = (Map<String, String>) item;
				return map.containsKey(key) && value.toString().equals(map.get(key));
			}

			@Override
			public void describeTo(Description description) {
				description.appendText("a map containing key: " + key + " and value: " + value);
			}
		});
	}

}

interface SampleService {

	@Get("testGet")
	public void testGet(@Named("name") String name);

	@Path("testPath")
	public void testPath(@Named("name") String name);

	@Delete("testDelete")
	void testDelete(@Named("id") int id);

	@Put("testPut")
	void testPut(@Named("id") int id);

	@Path("bla/{id}/{name}")
	void testPathWithParam(@Named("id") String id, @Named("name") String name);

	@Get("regex/{id:[0-9]*{0}}/bla")
	void testWithRegex(@Named("id") int id);

	@Get("regex-complex/{all:[0-9]*{0}}/ALL/{gone:[0-9]*}/GONE/{to:[0-9]*}/TO/{hell:[a-zA-Z0-9]*}/HELL")
	void testWithRegexComplex(@Named("all") int all, @Named("gone") int gone, @Named("to") int to,
			@Named("hell") String hell);
}
