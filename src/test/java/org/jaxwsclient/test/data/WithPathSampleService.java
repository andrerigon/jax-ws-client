package org.jaxwsclient.test.data;

import javax.inject.Named;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

public @Path("/prefix")
interface WithPathSampleService {
	@GET
	@Path("/testGet")
	public void testGet(@Named("name") String name);
}