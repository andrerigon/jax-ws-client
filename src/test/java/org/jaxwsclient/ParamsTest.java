package org.jaxwsclient;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.jaxwsclient.params.Parameters;
import org.jaxwsclient.params.ParametersSerializer;
import org.junit.Test;

public class ParamsTest {

	String[] names = { "name", "age" };

	@Test
	public void should_list_method_params_names() throws Exception {
		assertArrayEquals(names, new Parameters(sampleMethod(), "").names().toArray());
	}

	@Test
	public void should_create_params_map_with_simple_value() throws Exception {
		Map<String, Object> params = ParametersSerializer.paramsFor("andre", "name");

		assertTrue(params.containsKey("name"));
		assertTrue("andre".equals(params.get("name")));
	}

	@Test
	public void should_create_params_map_with_complex_object() throws Exception {
		Car car = newCar("fusca", 1972);

		Map<String, Object> params = ParametersSerializer.paramsFor(car, "car");
		assertEquals("fusca", params.get("model"));
		assertEquals(1972l, params.get("year"));
	}

	@Test
	public void should_create_params_map_with_list() throws Exception {
		final List<String> carList = Arrays.asList("mustang", "camaro");

		Map<String, Object> params = ParametersSerializer.paramsFor(carList, "carList");

		assertTrue(params.containsValue(carList));
	}

	@Test
	public void should_create_params_with_nested_complex_objects() throws Exception {

		Person p = newPerson("andre", newCar("fusca", 1972));

		Map<String, Object> params = ParametersSerializer.paramsFor(p, "person");
		assertEquals("andre", params.get("name"));
		assertEquals("fusca", this.<Map<String, Object>> get(params, "car").get("model"));
		assertEquals(1972l, this.<Map<String, Object>> get(params, "car").get("year"));

	}

	@Test
	public void should_create_params_from_enum() throws Exception {

		Map<String, Object> params = ParametersSerializer.paramsFor(Sex.MALE, "sex");

		assertEquals("MALE", params.get("sex").toString());
	}

	@Test
	public void should_create_params_from_enum_with_override() throws Exception {
		Map<String, Object> params = ParametersSerializer.paramsFor(Sex.FEMALE, "sex");

		assertEquals("FEMALE", params.get("sex").toString());

	}

	@Test
	public void should_do_nothing_when_inner_objects_are_null() throws Exception {
		Person p = newPerson("andre", null);

		Map<String, Object> params = ParametersSerializer.paramsFor(p, "person");

		assertEquals("andre", params.get("name"));
		assertFalse(params.containsKey("car"));
	}

	@Test
	public void should_not_send_null_list_params() throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		final List<String> carList = null;

		Map<String, Object> params = ParametersSerializer.paramsFor(carList, "carList");

		assertFalse(params.containsKey("carList"));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void should_serialize_list_with_inner_complex_objects() throws Exception {
		final List<Person> list = Arrays.asList(newPerson("andre", newCar("chevet", 1976)), newPerson("joao", newCar("fuscao", 1966)));

		Group group = new Group(list, "group1");

		Map<String, ?> map = ParametersSerializer.paramsFor(group, "group");

		assertEquals("group1", map.get("name"));

		Map<String, Object> andre = ((List<Map<String, Object>>) map.get("people")).get(0);

		assertEquals("andre", andre.get("name"));
		assertEquals("chevet", ((Map<String, Object>) andre.get("car")).get("model"));
		assertEquals(1976d, ((Map<String, Object>) andre.get("car")).get("year"));

		Map<String, Object> joao = ((List<Map<String, Object>>) map.get("people")).get(1);

		assertEquals("joao", joao.get("name"));
		assertEquals("fuscao", ((Map<String, Object>) joao.get("car")).get("model"));
		assertEquals(1966d, ((Map<String, Object>) joao.get("car")).get("year"));
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

	public static class Group {

		List<Person> people;

		String name;

		public Group(List<Person> people, String name) {
			super();
			this.people = people;
			this.name = name;
		}

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

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((car == null) ? 0 : car.hashCode());
			result = prime * result + ((name == null) ? 0 : name.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Person other = (Person) obj;
			if (car == null) {
				if (other.car != null)
					return false;
			} else if (!car.equals(other.car))
				return false;
			if (name == null) {
				if (other.name != null)
					return false;
			} else if (!name.equals(other.name))
				return false;
			return true;
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

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((model == null) ? 0 : model.hashCode());
			result = prime * result + year;
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Car other = (Car) obj;
			if (model == null) {
				if (other.model != null)
					return false;
			} else if (!model.equals(other.model))
				return false;
			if (year != other.year)
				return false;
			return true;
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

	@SuppressWarnings("unchecked")
	private <T> T get(Map<String, ?> params, String key) {
		return (T) params.get(key);
	}
}
