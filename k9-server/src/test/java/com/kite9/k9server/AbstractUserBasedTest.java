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
		String email = "thing"+userNumber+"@example.com";
		u = createUser(restTemplate, username, password, email);
		userUrl = u.getLink(Link.REL_SELF).getHref();
		jwtToken = getJwtToken(restTemplate, email, password);
	}
	
	@After
	public void removeUser() throws URISyntaxException {
		System.out.println("CLEANING UP ");
		deleteViaJwt(restTemplate, userUrl, jwtToken);
		try {
			Resources<UserResource> resources = retrieveUserViaJwt(restTemplate, jwtToken);
			if (resources.getContent().size() == 0) {
				return;
			} else {
				for (UserResource userResource : resources.getContent()) {
					Assert.assertNotEquals(userUrl, userResource.getLink(Link.REL_SELF).getHref());
				}
			}
		} catch (ResourceAccessException e) {
			// it's been deleted.
		}
	}
	
}
