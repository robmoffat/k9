package com.kite9.k9server.domain;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;

import org.junit.Assert;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;

import com.kite9.k9server.AbstractUserBasedTest;
import com.kite9.k9server.resource.DocumentResource;
import com.kite9.k9server.resource.ProjectResource;
import com.kite9.k9server.resource.RevisionResource;

public abstract class AbstractLifecycleTest extends AbstractUserBasedTest {

	public AbstractLifecycleTest() {
		super();
	}

	public RevisionResource createARevisionResource(DocumentResource forDocument) throws URISyntaxException {
		String doc = forDocument.getLink(Link.REL_SELF).getHref();
		RevisionResource r = new RevisionResource(doc, new Date(), userUrl, "renderedXML", null, null);  
		RequestEntity<RevisionResource> in = new RequestEntity<>(r, createHeaders(), HttpMethod.POST, new URI(getUrlBase()+"/api/revisions"));
		ResponseEntity<RevisionResource> rOut = restTemplate.exchange(in, RevisionResource.class);
		Assert.assertEquals(HttpStatus.CREATED, rOut.getStatusCode());
		
		// retrieve it
		in = new RequestEntity<>(createHeaders(), HttpMethod.GET, rOut.getHeaders().getLocation());
		rOut = restTemplate.exchange(in, RevisionResource.class);
		Assert.assertEquals("renderedXML", rOut.getBody().xml);
		Assert.assertNotNull(rOut.getBody().getLink("document").getHref());
		return rOut.getBody();
		
	}

	public DocumentResource createADocumentResource(ProjectResource forProject) throws URISyntaxException {
		DocumentResource d = new DocumentResource("My Document", "Some name for a document", forProject.getLink(Link.REL_SELF).getHref());
		RequestEntity<DocumentResource> in = new RequestEntity<>(d, createHeaders(), HttpMethod.POST, new URI(getUrlBase()+"/api/documents"));
		ResponseEntity<DocumentResource> dOut = restTemplate.exchange(in, DocumentResource.class);
		Assert.assertEquals(HttpStatus.CREATED, dOut.getStatusCode());
		
		// retrieve it
		in = new RequestEntity<>(createHeaders(), HttpMethod.GET, dOut.getHeaders().getLocation());
		dOut = restTemplate.exchange(in, DocumentResource.class);
		Assert.assertEquals(d, dOut.getBody());
		return dOut.getBody();
	}

	public DocumentResource updateADocumentResource(DocumentResource d, ProjectResource p, RevisionResource rOut) throws URISyntaxException {
		DocumentResource changed = new DocumentResource();
		changed.project = p.getLink(Link.REL_SELF).getHref();
		changed.currentRevision = rOut.getLink(Link.REL_SELF).getHref();
		changed.title = "Updated title";
		changed.dateCreated = d.dateCreated;
		changed.description = d.description;
		changed.lastUpdated = new Date();
	
		URI self = new URI(d.getLink(Link.REL_SELF).getHref());
		RequestEntity<DocumentResource> in = new RequestEntity<>(changed, createHeaders(), HttpMethod.PUT, self);
		ResponseEntity<DocumentResource> dOut = restTemplate.exchange(in, DocumentResource.class);
		Assert.assertTrue(dOut.getStatusCode().is2xxSuccessful());
		
		// retrieve it
		in = new RequestEntity<>(createHeaders(), HttpMethod.GET, dOut.getHeaders().getLocation());
		dOut = restTemplate.exchange(in, DocumentResource.class);
		Assert.assertEquals(changed.title, dOut.getBody().title);
		return dOut.getBody();
	}

	public ProjectResource createAProjectResource() throws URISyntaxException {
		ProjectResource pIn = new ProjectResource("Test Project 2", "Lorem Ipsum 1", "tp2", "");
		RequestEntity<ProjectResource> re = new RequestEntity<>(pIn, createHeaders(), HttpMethod.POST, new URI(getUrlBase()+"/api/projects"));
		
		ResponseEntity<ProjectResource> pOut = restTemplate.exchange(re, ProjectResource.class);
		Assert.assertEquals(HttpStatus.CREATED, pOut.getStatusCode());
		
		// retrieve it
		re = new RequestEntity<>(createHeaders(), HttpMethod.GET, pOut.getHeaders().getLocation());
		pOut = restTemplate.exchange(re, ProjectResource.class);
		
		
		return pOut.getBody();
	}

	public ProjectResource updateAProjectResource(ProjectResource pIn) throws URISyntaxException {
		pIn.description = "desc 2";
		RequestEntity<ProjectResource> re = new RequestEntity<>(pIn, createHeaders(), HttpMethod.PUT, new URI(pIn.getLink(Link.REL_SELF).getHref()));
		ResponseEntity<ProjectResource> pOut = restTemplate.exchange(re, ProjectResource.class);
		Assert.assertTrue(pOut.getStatusCode().is2xxSuccessful());
		
		// retrieve it
		re = new RequestEntity<>(createHeaders(), HttpMethod.GET, pOut.getHeaders().getLocation());
		pOut = restTemplate.exchange(re, ProjectResource.class);
				
		Assert.assertEquals(pIn.description, pOut.getBody().description);
		return pOut.getBody();
	}


	protected HttpHeaders createHeaders() {
		HttpHeaders out = createJWTTokenHeaders(jwtToken, MediaType.APPLICATION_JSON);
		return out;
		
	}
	
}