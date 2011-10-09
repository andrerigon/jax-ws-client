package br.com.vraptor.client.test.data;

import java.lang.reflect.Method;

public class Samples {

	public static Method sampleMethodWithRegex() throws SecurityException, NoSuchMethodException {
		return SampleService.class.getDeclaredMethod("testWithRegex", int.class);
	}

	public static Method sampleMethodWithRegexComplex() throws SecurityException, NoSuchMethodException {
		return SampleService.class.getDeclaredMethod("testWithRegexComplex", int.class, int.class, int.class,
				String.class);
	}

	public static Method samplePutMethod() throws SecurityException, NoSuchMethodException {
		return SampleService.class.getDeclaredMethod("testPut", int.class);
	}

	public static Method sampleMethodWithParamInThePath() throws SecurityException, NoSuchMethodException {
		return SampleService.class.getDeclaredMethod("testPathWithParam", String.class, String.class);
	}

	public static Method sampleDeleteMethod() throws SecurityException, NoSuchMethodException {
		return SampleService.class.getDeclaredMethod("testDelete", int.class);
	}

	public static Method samplePathMethod() throws SecurityException, NoSuchMethodException {
		return SampleService.class.getDeclaredMethod("testPath", String.class);
	}

	public static Method sampleGetMethod() throws SecurityException, NoSuchMethodException {
		return SampleService.class.getDeclaredMethod("testGet", String.class);
	}

	public static Method sampleMethodWithPathInClassLevel() throws SecurityException, NoSuchMethodException {
		return WithPathSampleService.class.getMethod("testGet", String.class);
	}

}

