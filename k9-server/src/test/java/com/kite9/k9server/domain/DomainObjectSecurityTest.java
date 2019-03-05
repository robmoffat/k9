package com.kite9.k9server.domain;

import java.net.URI;
import java.net.URISyntaxException;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Links;
import org.springframework.hateoas.Resources;
import org.springframework.web.client.ResourceAccessException;
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

		switchUser();
		
		Resources<DocumentResource> docs = getAllDocumentResources();
		Assert.assertTrue(docs.getContent().size() == 0);
		URI location = new URI(dr.getLink(Link.REL_SELF).getHref());

		try {
			DocumentResource ndr = getADocumentResource(location);
			Assert.fail("Shouldn't be allowed get");
		} catch (ResourceAccessException e) {
		}
		
		try {
			updateADocumentResource(dr, pr, rr);
			Assert.fail("Shouldn't be allowed update");
		} catch (Forbidden e) {
		}
		
		try {
			delete(location);
			Assert.fail("Shouldn't be allowed delete");
		} catch (ResourceNotFoundException e) {
		}
		
		try {
			delete(new URI(getUrlBase()+ "/api/documents"));
			Assert.fail("Shouldn't be allowed delete");
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
