## WHY?

![Build Status](https://secure.travis-ci.org/andrerigon/vraptor-client.png)

when writing a java client app for rest based service, you must have duplication.
You need to duplicate urls, paths, parameters type and order.
What if you could just use an interface and call it's methods?
It would send the correct http request, with all already done for you.
jax-ws-client aims to do that. It's a proxy handler implementation that will use a simple
which describes your server's implementation, building correctly a request to the service.

For example, imagine a rest controller:

```java

public class ClientController {

    @Override
    public String save(Client client) {
		log.info(String.format("Saving client with name %s and age %d", client.name, client.age));
		return "ok";
	}

}
```

now a Clients interface:

```java
public interface Clients {
    
   @POST
   @Path("save")
   String save(@Named("client") Client client);
}
```

int the client, you just have to inject the proxy implementation and use the interface:

```java
public class ClientTest {

    @Autowired
    Clients clients;

    @Test
	public void test_save_client() {
		Client client = new Client();
		client.age = 25;
		client.name = "andre";
		assertEquals("ok", clients.save(client));
	}

}
```

## INSTALL

<pre>
<code>git clone git://github.com/andrerigon/jax-ws-client.git
cd jax-ws-client
mvn install</code>
</pre>

## USAGE


The interfaces will need to use @Named annotation to indicate parameters name. Without that, paranamer won't be able to figure out params names.


The proxy handler classe is:

```java
org.jaxwsclient.handler.RestProxyHandler
```

it needs four constructor arguments:

* a class implementing **br.com.vraptor.client.RestClient** - it will be in charge of doing the http requests
* the base path for the request
* a class implementing **br.com.vraptor.client.ResultParser** - it will parse the response and deal with exceptions
* the class to be proxied

to create a proxy, you'll need to do:

```java
RestProxyHandler.newProxy(restClient, path, parser, clazz);
```

