package com.kite9.k9server.domain;

import java.net.URI;
import java.net.URISyntaxException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resources;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException.Forbidden;
import org.springframework.web.client.HttpClientErrorException.NotFound;

import com.kite9.k9server.domain.permission.ProjectRole;
import com.kite9.k9server.resource.DocumentResource;
import com.kite9.k9server.resource.MemberResource;
import com.kite9.k9server.resource.ProjectResource;
import com.kite9.k9server.resource.RevisionResource;
import com.kite9.k9server.resource.UserResource;

public class DomainObjectSecurityTest extends AbstractLifecycleTest {

	UserResource altUser;
	String altToken;
	
	@Before
	public void setupAltUser() throws Exception {
		String username = "altuser";
		String password = "facts11";
		altUser = createUser(restTemplate, username, password, "viewer@example.com");
		altToken = getJwtToken(restTemplate, username, password);
	}
	
	@Test
	public void ensureSecondUserCantModifyFirstUsersProject() throws Exception {
		String adminJwt = jwtToken;
		ProjectResource pr = createAProjectResource();
		DocumentResource dr  = createADocumentResource(pr, "http://localhost:"+port+"/public/templates/basic.xml");
		updateADocumentResource(dr);

		// switch to the viewer - should be able to retrieve one document
		MemberResource mr = createAMemberResource(pr, altUser.getLink(Link.REL_SELF).getHref());
		jwtToken = altToken;
		Resources<DocumentResource> docs = getAllDocumentResources();
		Assert.assertTrue(docs.getContent().size() == 1);
		attemptView(pr, dr, rr, true);
		attemptMutations(pr, dr, rr);
		
		// switch to a bad user- should be able to retried
		switchBadUser();
		docs = getAllDocumentResources();
		Assert.assertTrue(docs.getContent().size() == 0);
		attemptView(pr, dr, rr, false);
		attemptMutations(pr, dr, rr);
		
		// change the viewer to be a admin
		mr.projectRole = ProjectRole.ADMIN;
		jwtToken = adminJwt;
		updateAMemberResource(mr);
		jwtToken = altToken;
		deleteAndCheckDeleted(restTemplate, pr.getLink(Link.REL_SELF).getHref(), jwtToken, ProjectResource.class);
		
		// revert to the admin
		jwtToken = adminJwt;
	}

	public void attemptView(ProjectResource pr, DocumentResource dr, RevisionResource rr, boolean allowed) throws URISyntaxException {
		String href = dr.getLink(Link.REL_SELF).getHref();
		
		URI location = new URI(href);
	
		try {
			DocumentResource ndr = getADocumentResource(location);
			if (!allowed) {
				Assert.fail("Shouldn't be allowed get");
			} else {
				if (ndr == null) {
					Assert.fail("Returned null for get");
				}
				
				String href2 = dr.getLink("revisions").getHref();
				if (href2.contains("{?projection}")) {
					href2 = href2.substring(0, href2.indexOf("{?projection}"));
				}
				Resources<RevisionResource> revs = getAllRevisionResources(new URI(href2));
				Assert.assertEquals(1, revs.getContent().size());
			}
		} catch (Exception e) {
			if (allowed) {
				Assert.fail("Should be allowed get");
			}
		}
		
		
	}
	
	public void attemptMutations(ProjectResource pr, DocumentResource dr, RevisionResource rr) throws URISyntaxException {
		URI location = new URI(dr.getLink(Link.REL_SELF).getHref());
		
		try {
			updateADocumentResource(dr, pr, rr);
			Assert.fail("Shouldn't be allowed update");
		} catch (Forbidden e) {
		}
		
		try {
			delete(location);
			Assert.fail("Shouldn't be allowed delete of "+location);
		} catch (Forbidden e) {
		}
		
		try {
			delete(new URI(getUrlBase()+ "/api/documents"));
			Assert.fail("Shouldn't be allowed delete");
		} catch (NotFound e) {
		}
	}
	
	public void switchBadUser() throws URISyntaxException {
		String username = "badactor";
		String password = "facts11";
		u = createUser(restTemplate, username, password, "badthing3@example.com");
		jwtToken = getJwtToken(restTemplate, username, password);
	}
	
	public MemberResource createAMemberResource(ProjectResource forProject, String userUrl) throws URISyntaxException {
		MemberResource mr = new MemberResource(userUrl, forProject.getLink(Link.REL_SELF).getHref(), ProjectRole.VIEWER);
		RequestEntity<MemberResource> in = new RequestEntity<>(mr, createHeaders(), HttpMethod.POST, new URI(getUrlBase()+"/api/members"));
		ResponseEntity<MemberResource> rOut = restTemplate.exchange(in, MemberResource.class);
		Assert.assertEquals(HttpStatus.CREATED, rOut.getStatusCode());
		
		// retrieve it
		URI location = rOut.getHeaders().getLocation();
		MemberResource resource = getAMemberResource(location);
		Assert.assertNotNull(resource.getLink("project").getHref());
		return resource;
		
	}
	
}
