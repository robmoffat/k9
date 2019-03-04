package com.kite9.k9server.domain;

import java.net.URISyntaxException;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.web.client.HttpClientErrorException.Forbidden;

import com.kite9.k9server.resource.DocumentResource;
import com.kite9.k9server.resource.ProjectResource;
import com.kite9.k9server.resource.RevisionResource;

public class DomainObjectSecurityTest extends AbstractLifecycleTest {

	
	@Test
	public void ensureSecondUserCantModifyFirstUsersProject() throws Exception {
		ProjectResource pr = createAProjectResource();
		DocumentResource dr  = createADocumentResource(pr);
		RevisionResource rr = createARevisionResource(dr);
		updateADocumentResource(dr, pr, rr);

		
		try {
			switchUser();
			updateADocumentResource(dr, pr, rr);
			Assert.fail("Shouldn't be allowed update");
		} catch (Forbidden e) {
		}
		
	}
	
	public void switchUser() throws URISyntaxException {
		String username = "badactor";
		String password = "facts11";
		u = createUser(restTemplate, username, password, "thing3@example.com");
		jwtToken = getJwtToken(restTemplate, username, password);
	}
	
}
