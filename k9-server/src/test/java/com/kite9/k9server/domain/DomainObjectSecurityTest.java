package com.kite9.k9server.domain;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.List;

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

import com.kite9.k9server.command.Command;
import com.kite9.k9server.command.domain.AddMembers;
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
		String email = "viewer@example.com";
		altUser = createUser(restTemplate, username, password, email);
		altToken = getJwtToken(restTemplate, email, password);
	}
	
	
	
	@Override
	public void removeUser() throws URISyntaxException {
		deleteViaJwt(restTemplate, altUser.getLink(Link.REL_SELF).getHref(), altToken);
		super.removeUser();
	}



	@Test
	public void ensureSecondUserCantModifyFirstUsersProject() throws Exception {
		String adminJwt = jwtToken;
		ProjectResource pr = createAProjectResource();
		DocumentResource dr  = createADocumentResource(pr, "http://localhost:"+port+"/public/templates/basic.xml");
		updateADocumentResource(dr);

		// switch to the viewer - should be able to retrieve one document
		pr = createAMemberResource(pr, altUser.email, ProjectRole.VIEWER);
		jwtToken = altToken;
		Resources<DocumentResource> docs = getAllDocumentResources();
		Assert.assertTrue(docs.getContent().size() == 1);
		attemptView(dr, true);
		attemptMutations(dr);
		
		// switch to a bad user- should be able to retried
		switchBadUser();
		docs = getAllDocumentResources();
		Assert.assertTrue(docs.getContent().size() == 0);
		attemptView(dr, false);
		attemptMutations(dr);
		
		// change the viewer to be a admin
		deleteViaJwt(restTemplate, u.getLink(Link.REL_SELF).getHref(), jwtToken);
		jwtToken = adminJwt;
		createAMemberResource(pr, altUser.email, ProjectRole.ADMIN);
		jwtToken = altToken;
		deleteAndCheckDeleted(restTemplate, pr.getLink(Link.REL_SELF).getHref(), jwtToken, ProjectResource.class);
		
		// revert to the admin
		jwtToken = adminJwt;
	}

	public void attemptView(DocumentResource dr, boolean allowed) throws URISyntaxException {
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
	
	public void attemptMutations(DocumentResource dr) throws URISyntaxException {
		URI location = new URI(dr.getLink(Link.REL_SELF).getHref());
		
		try {
			updateADocumentResource(dr);
			Assert.fail("Shouldn't be allowed update");
		} catch (Exception e) {
		}
		
		try {
			delete(location);
			Assert.fail("Shouldn't be allowed delete of "+location);
		} catch (Exception e) {
		}
		
		try {
			delete(new URI(getUrlBase()+ "/api/documents"));
			Assert.fail("Shouldn't be allowed delete");
		} catch (Exception e) {
		}
	}
	
	public void switchBadUser() throws URISyntaxException {
		String username = "badactor";
		String password = "facts11";
		String email = "badthing3@example.com";
		u = createUser(restTemplate, username, password, email);
		jwtToken = getJwtToken(restTemplate, email, password);
	}
	
	public ProjectResource createAMemberResource(ProjectResource forProject, String email, ProjectRole pr) throws URISyntaxException {
		AddMembers am = new AddMembers();
		am.emailAddresses = email;
		am.role = pr;
		am.setSubjectUri(forProject.getLink(Link.REL_SELF).getHref());
		
		RequestEntity<List<Command>> in = new RequestEntity<>(new CommandList(am), createHeaders(), HttpMethod.POST, getAdminUri());
		ResponseEntity<ProjectResource> rOut = restTemplate.exchange(in, ProjectResource.class);
		Assert.assertEquals(HttpStatus.OK, rOut.getStatusCode());
		
		// retrieve it
		URI location = rOut.getHeaders().getLocation();
		ProjectResource resource = getAProjectResource(new URI(forProject.getLink(Link.REL_SELF).getHref()));
		Assert.assertNotNull(resource.getLink("project").getHref());
		return resource;
		
	}
	
}
