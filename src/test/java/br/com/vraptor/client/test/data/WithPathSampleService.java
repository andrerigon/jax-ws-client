package br.com.vraptor.client.test.data;

import javax.inject.Named;

import br.com.caelum.vraptor.Get;
import br.com.caelum.vraptor.Path;

public @Path("/prefix")
interface WithPathSampleService {
	@Get("/testGet")
	public void testGet(@Named("name") String name);
}