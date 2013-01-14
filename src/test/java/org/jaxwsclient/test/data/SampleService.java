package org.jaxwsclient.test.data;

import java.util.List;

import javax.inject.Named;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;



public interface SampleService {

	@GET
	@Path("testGet")
	public void testGet(@Named("name") String name) throws CheckedException;

	@DELETE
	@Path("testDelete")
	void testDelete(@Named("id") int id);

	@PUT
	@Path("testPut")
	void testPut(@Named("id") int id);

	@GET
	@Path("bla/{id}/{name}")
	void testPathWithParam(@Named("id") String id, @Named("name") String name);

	@GET
	@Path("regex/{id:[0-9]*{0}}/bla")
	void testWithRegex(@Named("id") Integer id);

	@GET
	@Path("regex-complex/{all:[0-9]*{0}}/ALL/{gone:[0-9]*}/GONE/{to:[0-9]*}/TO/{hell:[a-zA-Z0-9]*}/HELL")
	void testWithRegexComplex(@Named("all") int all, @Named("gone") int gone, @Named("to") int to,
			@Named("hell") String hell);

	@GET
	@Path("regex-query/{test:[0-9]+}/xit")
	void testWithQueryString(@Named("test") Long test, @Named("query") List<Long> query);

}
