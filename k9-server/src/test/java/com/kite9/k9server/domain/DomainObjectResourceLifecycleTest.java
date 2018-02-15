package com.kite9.k9server.domain;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;

import com.kite9.k9server.AbstractAuthenticatedIT;

public class DomainObjectResourceLifecycleTest extends AbstractAuthenticatedIT {
	
	protected String documentsUrl = urlBase + "/api/documents";
	protected String projectUrl = urlBase + "/api/projects";
	protected String revisionsUrl = urlBase + "/api/revisions";
	
	@Test
	public void testLifeCycle() throws Exception {
		// create a first project
		ProjectResource pOut = createAProjectResource();
				
		// create a document on the project
		DocumentResource dOut = createADocumentResource(pOut);
		
		// add a revision to the document
		RevisionResource rOut = createARevisionResource(dOut);
		
		
		deleteAndCheckDeleted(restTemplate, rOut.getLink(Link.REL_SELF).getHref(), u, RevisionResource.class);
		deleteAndCheckDeleted(restTemplate, dOut.getLink(Link.REL_SELF).getHref(), u, DocumentResource.class);
		deleteAndCheckDeleted(restTemplate, pOut.getLink(Link.REL_SELF).getHref(), u, ProjectResource.class);
				
	}
	
	public RevisionResource createARevisionResource(DocumentResource forDocument) throws URISyntaxException {
		RevisionResource r = new RevisionResource(forDocument.getLink(Link.REL_SELF).getHref(), "someXML", "abc123", new Date(), userUrl, "renderedXML", null, null);  
		RequestEntity<RevisionResource> in = new RequestEntity<>(r, createKite9AuthHeaders(u.getApi()), HttpMethod.POST, new URI(revisionsUrl));
		ResponseEntity<RevisionResource> rOut = restTemplate.exchange(in, RevisionResource.class);
		Assert.assertEquals(HttpStatus.CREATED, rOut.getStatusCode());
		Assert.assertEquals(r, rOut.getBody());
		return rOut.getBody();
		
	}
	
	public DocumentResource createADocumentResource(ProjectResource forProject) throws URISyntaxException {
		DocumentResource d = new DocumentResource("My Document", "Some name for a document", forProject.getLink(Link.REL_SELF).getHref());
		RequestEntity<DocumentResource> in = new RequestEntity<>(d, createKite9AuthHeaders(u.getApi()), HttpMethod.POST, new URI(documentsUrl));
		ResponseEntity<DocumentResource> dOut = restTemplate.exchange(in, DocumentResource.class);
		Assert.assertEquals(HttpStatus.CREATED, dOut.getStatusCode());
		Assert.assertEquals(d, dOut.getBody());
		return dOut.getBody();
	}

	public ProjectResource createAProjectResource() throws URISyntaxException {
		ProjectResource pIn = new ProjectResource("Test Project 2", "Lorem Ipsum 1", "tp2", "");
		RequestEntity<ProjectResource> re = new RequestEntity<>(pIn, createKite9AuthHeaders(u.getApi()), HttpMethod.POST, new URI(projectUrl));
		
		ResponseEntity<ProjectResource> pOut = restTemplate.exchange(re, ProjectResource.class);
		Assert.assertEquals(pIn, pOut.getBody());
		Assert.assertEquals(HttpStatus.CREATED, pOut.getStatusCode());
		return pOut.getBody();
	}

	
	
	
}
