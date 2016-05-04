package com.smart;
import static org.junit.Assert.*;
import guru.nidi.ramltester.RamlDefinition; 
import guru.nidi.ramltester.RamlLoaders; 
import guru.nidi.ramltester.jaxrs.CheckingWebTarget; 
import guru.nidi.ramltester.junit.RamlMatchers; 
import org.jboss.resteasy.client.jaxrs.ResteasyClient; 
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.smart.services.VertxRunner;


public class APITest {

	private static final RamlDefinition api = RamlLoaders.fromClasspath().load("/raml/api/smart.raml")
			.assumingBaseUri("http://localhost:6553/");

	private ResteasyClient client = new ResteasyClientBuilder().build();
	private CheckingWebTarget checking;

	@BeforeClass
	public static void bootServer() {
		VertxRunner.vertxRunner();
	}

	@Before
	public void createTarget() {
		checking = api.createWebTarget(client.target("http://localhost:6553"));
	}

	@Test
	public void testHelloEndpoint() {
		checking.path("/smart").request().get();
		Assert.assertThat(checking.getLastReport(), RamlMatchers.hasNoViolations());
	}
}
