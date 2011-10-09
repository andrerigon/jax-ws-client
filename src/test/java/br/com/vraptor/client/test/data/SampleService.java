package br.com.vraptor.client.test.data;

import java.util.List;

import javax.inject.Named;

import br.com.caelum.vraptor.Delete;
import br.com.caelum.vraptor.Get;
import br.com.caelum.vraptor.Path;
import br.com.caelum.vraptor.Put;

public interface SampleService {

	@Get("testGet")
	public void testGet(@Named("name") String name) throws CheckedException;

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

	@Get("regex-query/{test:[0-9]+}/xit")
	void testWithQueryString(@Named("test") Long test, @Named("query") List<Long> query);
}
