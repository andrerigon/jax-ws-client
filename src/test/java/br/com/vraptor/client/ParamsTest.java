package br.com.vraptor.client;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import br.com.vraptor.client.params.ParameterInfo;
import br.com.vraptor.client.params.Parameters;

import com.google.common.collect.ImmutableList;

public class ParamsTest {

	String[] names = { "name", "age" };

	@Test
	public void should_list_method_params_names() throws Exception {
		assertArrayEquals(names, names(Parameters.paramsInfoFor(sampleMethod())));
	}

	private Object[] names(ImmutableList<ParameterInfo> paramsInfo) {
		List<String> list = new ArrayList<String>();
		for( ParameterInfo info : paramsInfo ){
			list.add( info.name());
		}
		return list.toArray();
	}

	@Test
	public void should_create_params_map_with_simple_value() throws Exception {
		Map<String, Object> params = Parameters.paramsFor("andre", "name");

		assertTrue(params.containsKey("name"));
		assertTrue("andre".equals(params.get("name")));
	}

	@Test
	public void should_create_params_map_with_complex_object() throws Exception {
		Car car = newCar("fusca", 1972);

		Map<String, Object> params = Parameters.paramsFor(car, "car");

		assertEquals("fusca", params.get("car.model"));
		assertEquals(1972, params.get("car.year"));
	}

	@Test
	public void should_create_params_map_with_list() throws Exception {
		final List<String> carList = Arrays.asList("mustang", "camaro");

		Map<String, Object> params = Parameters.paramsFor(carList, "carList");

		assertTrue(params.containsValue(carList));
	}

	@Test
	public void should_create_params_with_nested_complex_objects() throws Exception {

		Person p = newPerson("andre", newCar("fusca", 1972));

		Map<String, Object> params = Parameters.paramsFor(p, "person");

		assertEquals("andre", params.get("person.name"));
		assertEquals("fusca", params.get("person.car.model"));
		assertEquals(1972, params.get("person.car.year"));

	}

	@Test
	public void should_create_params_from_enum() throws Exception {

		Map<String, Object> params = Parameters.paramsFor(Sex.MALE, "sex");

		assertEquals("MALE", params.get("sex").toString());
	}

	@Test
	public void should_create_params_from_enum_with_override() throws Exception {
		Map<String, Object> params = Parameters.paramsFor(Sex.FEMALE, "sex");

		assertEquals("FEMALE", params.get("sex").toString());

	}

	@Test
	public void should_do_nothing_when_inner_objects_are_null() throws Exception {
		Person p = newPerson("andre", null);

		Map<String, Object> params = Parameters.paramsFor(p, "person");

		assertEquals("andre", params.get("person.name"));
		assertFalse(params.containsKey("person.car"));
	}

	private Person newPerson(String name, Car car) {
		Person p = new Person();
		p.name = name;
		p.car = car;
		return p;
	}

	private Car newCar(String model, int year) {
		Car car = new Car();
		car.model = model;
		car.year = year;
		return car;

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

	public static enum Sex {
		MALE, FEMALE {
			@Override
			public boolean isComplex() {
				return true;
			}
		};

		public boolean isComplex() {
			return false;
		}
	}

	void method(String name, int age) {
	}

	private Method sampleMethod() throws Exception {
		return ParamsTest.class.getDeclaredMethod("method", String.class, int.class);
	}
}
