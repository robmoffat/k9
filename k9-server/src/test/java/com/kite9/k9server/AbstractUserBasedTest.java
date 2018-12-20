package com.kite9.k9server;

import java.net.URISyntaxException;

import org.junit.After;
import org.junit.Before;
import org.springframework.hateoas.Link;

import com.kite9.k9server.resource.UserResource;

public abstract class AbstractUserBasedTest extends AbstractAuthenticatedIT {

	protected UserResource u;
	protected String userUrl;
	
	@Before
	public void withUser() throws URISyntaxException {
		u = createUser(restTemplate, "abc1234", "facts", "thing2@example.com");
		userUrl = u.getLink(Link.REL_SELF).getHref();
	}
	
	@After
	public void removeUser() throws URISyntaxException {
		delete(restTemplate, userUrl, u);
	}
}
