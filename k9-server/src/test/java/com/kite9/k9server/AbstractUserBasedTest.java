package com.kite9.k9server;

import java.net.URISyntaxException;

import org.junit.After;
import org.junit.Before;
import org.springframework.hateoas.Link;

import com.kite9.k9server.resource.UserResource;

public abstract class AbstractUserBasedTest extends AbstractAuthenticatedIT {

	protected UserResource u;
	protected String userUrl;
	protected String jwtToken;
	
	@Before
	public void withUser() throws URISyntaxException {
		String username = "abc1234";
		String password = "facts";
		u = createUser(restTemplate, username, password, "thing2@example.com");
		userUrl = u.getLink(Link.REL_SELF).getHref();
		jwtToken = getJwtToken(restTemplate, username, password);
	}
	
	@After
	public void removeUser() throws URISyntaxException {
		delete(restTemplate, userUrl, "abcd1234", "facts");
	}
}
