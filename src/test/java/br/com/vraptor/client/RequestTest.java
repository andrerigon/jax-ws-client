package br.com.vraptor.client;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

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
import br.com.vraptor.client.handler.RestProxyHandler;

@RunWith(MockitoJUnitRunner.class)
public class RequestTest {

	@Mock
	RestClient client;

	String path = "/jedi/";

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

	private Method sampleDeleteMethod() throws SecurityException, NoSuchMethodException {
		return SampleService.class.getDeclaredMethod("testDelete", int.class);
	}

	private Method samplePathMethod() throws SecurityException, NoSuchMethodException {
		return SampleService.class.getDeclaredMethod("testPath", String.class);
	}

	private RestProxyHandler restProxyHandler() {
		return new RestProxyHandler(client, path, mock(ResultParser.class));
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
}
