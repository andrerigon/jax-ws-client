package br.com.vraptor.client;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.Test;

public class ParamsTest {

	String[] names = { "name", "age" };

	@Test
	public void should_list_method_params_names() throws Exception {
		assertArrayEquals(names, Parameters.namesFor(sampleMethod()));
	}

	@Test
	public void should_create_params_map_with_simple_value() throws Exception {
		Map<String, Object> params = Parameters.paramsFor("andre", "name");

		assertTrue(params.containsKey("name"));
		assertTrue("andre".equals(params.get("name")));
	}

	@Test
	public void should_create_params_map_with_complex_object() throws Exception {
		Car car = new Car();
		car.model = "fusca";
		car.year = 1972;

		Map<String, Object> params = Parameters.paramsFor(car, "car");

		assertEquals("fusca", params.get("car.model"));
		assertEquals("1972", params.get("car.year"));
	}

	@Test
	public void should_create_params_map_with_list() throws Exception {
		final List<String> carList = new ArrayList<String>();
		carList.add("mustang");
		carList.add("camaro");

		Map<String, Object> params = Parameters.paramsFor(carList, "carList");

		assertTrue(params.containsValue(carList));
	}

	public static class Person {
		private Car car;
		private String name;

		public Car getCar() {
			return car;
		}

		public String getName() {
			return name;
		}

	}

	public static class Car {
		private String model;
		private int year;

		public String getModel() {
			return model;
		}

		public int getYear() {
			return year;
		}

	}

	void method(String name, int age) {
	}

	private Method sampleMethod() throws Exception {
		return ParamsTest.class.getDeclaredMethod("method", String.class, int.class);
	}
}
