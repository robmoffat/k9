package com.kite9.k9server.docker;

import java.net.URISyntaxException;

import org.junit.After;
import org.junit.Before;
import org.springframework.hateoas.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.kite9.k9server.domain.User;

/**
 * Makes sure there is a new user available to send REST requests with.
 * 
 * @author robmoffat
 *
 */
public class AbstractAuthenticatedIT extends AbstractRestIT {

	protected RestTemplate restTemplate = getRestTemplate();
	protected User u;
	protected String userUrl;
	
	@Before
	public void withUser() throws URISyntaxException {
		ResponseEntity<Resource<User>> userEntity = createUser(restTemplate, "abc1234", "facts", "thing2@example.com");
		u = userEntity.getBody().getContent();
		userUrl = userEntity.getHeaders().getLocation().toString();
	}
	
	@After
	public void removeUser() throws URISyntaxException {
		delete(restTemplate, userUrl, u);
	}
		
}
