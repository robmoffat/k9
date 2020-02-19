package com.kite9.k9server;

import org.junit.Before;
import org.springframework.web.client.RestTemplate;

/**
 * Makes sure there is a new user available to send REST requests with.
 * 
 * @author robmoffat
 *
 */
public abstract class AbstractAuthenticatedIT extends AbstractRestIT {

	protected RestTemplate restTemplate;
	protected String applicationToken;
	
	@Before
	public void setupRestTemplate() {
		this.restTemplate = getRestTemplate();
		
	}
}
