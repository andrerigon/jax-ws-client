## WHY?

![Build Status](https://secure.travis-ci.org/andrerigon/vraptor-client.png)

when writing a java client app for rest based service, you must have duplication.
You need to duplicate urls, paths, parameters type and order.
What if you could just use an interface and call it's methods?
It would send the correct http request, with all already done for you.
vraptor-client aims to do that. It's a proxy handler implementation that will use the same
interface that you use in the server's implementation, building correctly a request to the service.

For example, imagine a vraptor controller:

```java
@Resource
public class ClientController implements Clients{

    @Override
    public String save(Client client) {
		log.info(String.format("Saving client with name %s and age %d", client.name, client.age));
		return "ok";
	}

}
```

it implements Clients interface:

```java
public interface Clients {

   @Post("save")
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
<code>git clone git://github.com/andrerigon/vraptor-client.git
cd vraptor-client
mvn install</code>
</pre>

## USAGE

vraptor does not use interfaces to obtain route information by default.
To do that, you need to let vraptor use interfaces to obtain controller's information, using
a customized route parser.

There's an implementation at: https://gist.github.com/1149159

Also, the controller's interfaces will need to use @Named annotation to indicate parameters name. Without that, paranamer won't be able to figure out params names.


The proxy handler classe is:

```java
br.com.vraptor.client.handler.RestProxyHandler
```

it needs three constructor arguments:

* a class implementing **br.com.vraptor.client.RestClient** - it will be in charge of doing the http requests
* the base path for the request
* a class implementing **br.com.vraptor.client.ResultParser** - it will parse the response and deal with exceptions

to create a proxy, you'll need to do:

```java
Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), new Class<?>[] { clazz },
				new RestProxyHandler(restClient, path, parser))
```

if you use spring, there is a factory bean to create the services for you

```xml
<bean name="factory" class=" br.com.vraptor.client.spring.SpringRestFactory" />
```

it will need some beans:

* a class implementing: **br.com.vraptor.client.RestClient** - to make the http requests.
* the base path for the request.
* a class implementing: **br.com.vraptor.client.classprovider.RestClassesProvider** - it will inform which interfaces will be intercepted. If you want, you can use a use ClasspathScannerRestClassesProvider, and provide a base package to scan. Any interfaces will be loaded.
* and finally, a class implementing: **br.com.vraptor.client.ResultParser** - it will parse the result from the request.

