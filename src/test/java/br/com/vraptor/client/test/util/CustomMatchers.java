package br.com.vraptor.client.test.util;

import java.util.Map;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.mockito.Matchers;

public class CustomMatchers {
	public static Map<String, Object> aMapWithKeyValue(final String key, final Object value) {
		return Matchers.argThat(new BaseMatcher<Map<String, Object>>() {

			@Override
			public boolean matches(Object item) {
				@SuppressWarnings("unchecked")
				Map<String, Object> map = (Map<String, Object>) item;
				return map.containsKey(key) && value.toString().equals(map.get(key).toString());
			}

			@Override
			public void describeTo(Description description) {
				description.appendText("a map containing key: " + key + " and value: " + value);
			}
		});
	}
}
