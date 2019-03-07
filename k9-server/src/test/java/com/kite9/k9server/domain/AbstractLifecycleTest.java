package com.kite9.k9server.domain;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;

import org.junit.Assert;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resources;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;

import com.kite9.k9server.AbstractUserBasedTest;
import com.kite9.k9server.resource.DocumentResource;
import com.kite9.k9server.resource.MemberResource;
import com.kite9.k9server.resource.ProjectResource;
import com.kite9.k9server.resource.RevisionResource;

public abstract class AbstractLifecycleTest extends AbstractUserBasedTest {

	public static final String EMPTY_DOCUMENT = "<svg xmlns=\"http://www.w3.org/2000/svg\" id=\"0\"></svg>";


	public AbstractLifecycleTest() {
		super();
	}

	public RevisionResource createARevisionResource(DocumentResource forDocument) throws URISyntaxException {
		String doc = forDocument.getLink(Link.REL_SELF).getHref();
		RevisionResource r = new RevisionResource(doc, new Date(), userUrl, EMPTY_DOCUMENT, null, null);  
		RequestEntity<RevisionResource> in = new RequestEntity<>(r, createHeaders(), HttpMethod.POST, new URI(getUrlBase()+"/api/revisions"));
		ResponseEntity<RevisionResource> rOut = restTemplate.exchange(in, RevisionResource.class);
		Assert.assertEquals(HttpStatus.CREATED, rOut.getStatusCode());
		
		// retrieve it
		URI location = rOut.getHeaders().getLocation();
		RevisionResource resource = getARevisionResource(location);
		Assert.assertEquals(EMPTY_DOCUMENT, resource.xml);
		Assert.assertNotNull(resource.getLink("document").getHref());
		return resource;
		
	}

	public RevisionResource getARevisionResource(URI location) {
		RequestEntity<RevisionResource> in;
		ResponseEntity<RevisionResource> rOut;
		in = new RequestEntity<>(createHeaders(), HttpMethod.GET, location);
		rOut = restTemplate.exchange(in, RevisionResource.class);
		RevisionResource resource = rOut.getBody();
		return resource;
	}
	
	public MemberResource getAMemberResource(URI location) {
		RequestEntity<MemberResource> in;
		ResponseEntity<MemberResource> rOut;
		in = new RequestEntity<>(createHeaders(), HttpMethod.GET, location);
		rOut = restTemplate.exchange(in, MemberResource.class);
		MemberResource resource = rOut.getBody();
		return resource;
	}

	public DocumentResource createADocumentResource(ProjectResource forProject) throws URISyntaxException {
		DocumentResource d = new DocumentResource("My Document", "Some name for a document", forProject.getLink(Link.REL_SELF).getHref());
		RequestEntity<DocumentResource> in = new RequestEntity<>(d, createHeaders(), HttpMethod.POST, new URI(getUrlBase()+"/api/documents"));
		ResponseEntity<DocumentResource> dOut = restTemplate.exchange(in, DocumentResource.class);
		Assert.assertEquals(HttpStatus.CREATED, dOut.getStatusCode());
		return getADocumentResource(dOut.getHeaders().getLocation());
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

		changed = getADocumentResource(dOut.getHeaders().getLocation());
		Assert.assertEquals("Updated title", changed.title);
		return changed;
	}
	
	public MemberResource updateAMemberResource(MemberResource mr) throws URISyntaxException {
		RequestEntity<MemberResource> in = new RequestEntity<>(mr, createHeaders(), HttpMethod.PUT, new URI(mr.getLink(Link.REL_SELF).getHref()));
		ResponseEntity<MemberResource> dOut = restTemplate.exchange(in, MemberResource.class);
		return dOut.getBody();
	}

	public DocumentResource getADocumentResource(URI location) {
		RequestEntity<DocumentResource> in;
		ResponseEntity<DocumentResource> dOut;
		in = new RequestEntity<>(createHeaders(), HttpMethod.GET, location);
		dOut = restTemplate.exchange(in, DocumentResource.class);
		return dOut.getBody();
	}

	static int stubNumber = 1;
	
	public ProjectResource createAProjectResource() throws URISyntaxException {
		stubNumber++;
		ProjectResource pIn = new ProjectResource("Test Project 2", "Lorem Ipsum 1", "tp"+stubNumber, "");
		RequestEntity<ProjectResource> re = new RequestEntity<>(pIn, createHeaders(), HttpMethod.POST, new URI(getUrlBase()+"/api/projects"));
		
		ResponseEntity<ProjectResource> pOut = restTemplate.exchange(re, ProjectResource.class);
		Assert.assertEquals(HttpStatus.CREATED, pOut.getStatusCode());
		return getAProjectResource(pOut.getHeaders().getLocation());
	}

	public ProjectResource getAProjectResource(URI location) {
		RequestEntity<ProjectResource> re;
		ResponseEntity<ProjectResource> pOut;
		re = new RequestEntity<>(createHeaders(), HttpMethod.GET, location);
		pOut = restTemplate.exchange(re, ProjectResource.class);
		return pOut.getBody();
	}
	
	public Resources<DocumentResource> getAllDocumentResources() throws URISyntaxException {
		RequestEntity<DocumentResource> re = new RequestEntity<>(createHeaders(), HttpMethod.GET, new URI(getUrlBase()+"/api/documents"));
		ParameterizedTypeReference<Resources<DocumentResource>> ptr = new ParameterizedTypeReference<Resources<DocumentResource>>(){};
		ResponseEntity<Resources<DocumentResource>> pOut = restTemplate.exchange(re, ptr);
		return pOut.getBody();
	}
	
	public Resources<RevisionResource> getAllRevisionResources(URI u) {
		RequestEntity<DocumentResource> re = new RequestEntity<>(createHeaders(), HttpMethod.GET, u);
		ParameterizedTypeReference<Resources<RevisionResource>> ptr = new ParameterizedTypeReference<Resources<RevisionResource>>(){};
		ResponseEntity<Resources<RevisionResource>> pOut = restTemplate.exchange(re, ptr);
		return pOut.getBody();
	}

	public ProjectResource updateAProjectResource(ProjectResource pIn) throws URISyntaxException {
		pIn.description = "desc 2";
		RequestEntity<ProjectResource> re = new RequestEntity<>(pIn, createHeaders(), HttpMethod.PUT, new URI(pIn.getLink(Link.REL_SELF).getHref()));
		ResponseEntity<ProjectResource> pOut = restTemplate.exchange(re, ProjectResource.class);
		Assert.assertTrue(pOut.getStatusCode().is2xxSuccessful());
		return getAProjectResource(pOut.getHeaders().getLocation());
	}


	protected HttpHeaders createHeaders() {
		HttpHeaders out = createJWTTokenHeaders(jwtToken, MediaType.APPLICATION_JSON);
		return out;	
	}
	
	public void delete(URI url) throws URISyntaxException {
		delete(restTemplate, url.toString(), jwtToken);
	}
}