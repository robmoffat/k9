package com.kite9.k9server;

import java.net.URISyntaxException;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resources;
import org.springframework.web.client.ResourceAccessException;

import com.kite9.k9server.resource.UserResource;


public abstract class AbstractUserBasedTest extends AbstractAuthenticatedIT {

	protected UserResource u;
	protected String userUrl;
	protected String jwtToken;
	private static int userNumber = 0;
	
	@Before
	public void withUser() throws URISyntaxException {
		userNumber++;
		String username ="abc1234"+userNumber;
		String password = "facts";
		u = createUser(restTemplate, username, password, "thing"+userNumber+"@example.com");
		userUrl = u.getLink(Link.REL_SELF).getHref();
		jwtToken = getJwtToken(restTemplate, username, password);
	}
	
	@After
	public void removeUser() throws URISyntaxException {
		delete(restTemplate, userUrl, jwtToken);
		try {
			Resources<UserResource> resources = retrieveUserViaJwt(restTemplate, jwtToken);
			Assert.assertEquals(0, resources.getContent().size());
		} catch (ResourceAccessException e) {
			// it's been deleted.
		}
	}
	
}
