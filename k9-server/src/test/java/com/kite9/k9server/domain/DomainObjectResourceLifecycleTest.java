package com.kite9.k9server.domain;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;

import com.kite9.k9server.AbstractUserBasedTest;
import com.kite9.k9server.adl.format.media.MediaTypes;
import com.kite9.k9server.domain.revision.RevisionController;
import com.kite9.k9server.resource.DocumentResource;
import com.kite9.k9server.resource.ProjectResource;
import com.kite9.k9server.resource.RevisionResource;

@TestPropertySource(properties = { 
		"logging.level.org.hibernate=TRACE",
		"logging.level.org.hibernate.type.descriptor.sql.BasicBinder=TRACE",
		"spring.datasource.username=sa1",
		"spring.datasource.password=abc",
		"spring.h2.console.enabled=true",
		"spring.h2.console.path=/console"})
public class DomainObjectResourceLifecycleTest extends AbstractUserBasedTest {
	
	protected String documentsUrl = urlBase + "/api/documents";
	protected String projectUrl = urlBase + "/api/projects";
	protected String revisionsUrl = urlBase + "/api/revisions";
		
	@Test
	public void testLifeCycle() throws Exception {
		ProjectResource pOut = createAProjectResource();
		pOut = updateAProjectResource(pOut);
		
		DocumentResource dOut = createADocumentResource(pOut);

		RevisionResource rOut = createARevisionResource(dOut);
		rOut = updateARevisionResource(rOut);

		dOut = updateADocumentResource(dOut, pOut, rOut);

		
		// get the latest resource via the revision
		URI uri = new URI(rOut.getLink(RevisionController.CONTENT_REL).getHref());
		byte[] input = getADL(uri);
		Assert.assertTrue(new String(input).contains("some new xml"));
//		
//		// get the latest resource via the document, in output form
//		byte[] output = getSVG(uri);
//		Assert.assertEquals("renderedXML", new String(output));
		
		deleteAndCheckDeleted(restTemplate, pOut.getLink(Link.REL_SELF).getHref(), jwtToken, ProjectResource.class);
	}
	
	public RevisionResource createARevisionResource(DocumentResource forDocument) throws URISyntaxException {
		String doc = forDocument.getLink(Link.REL_SELF).getHref();
		RevisionResource r = new RevisionResource(doc, new Date(), userUrl, "renderedXML", null, null);  
		RequestEntity<RevisionResource> in = new RequestEntity<>(r, createHeaders(), HttpMethod.POST, new URI(revisionsUrl));
		ResponseEntity<RevisionResource> rOut = restTemplate.exchange(in, RevisionResource.class);
		Assert.assertEquals(HttpStatus.CREATED, rOut.getStatusCode());
		
		// retrieve it
		in = new RequestEntity<>(createHeaders(), HttpMethod.GET, rOut.getHeaders().getLocation());
		rOut = restTemplate.exchange(in, RevisionResource.class);
		Assert.assertEquals("renderedXML", rOut.getBody().xml);
		Assert.assertNotNull(rOut.getBody().getLink("document").getHref());
		return rOut.getBody();
		
	}
	
	public RevisionResource updateARevisionResource(RevisionResource r) throws URISyntaxException {
		r.xml = "<svg>some new xml</svg>";
		RequestEntity<RevisionResource> in = new RequestEntity<>(r, createHeaders(), HttpMethod.PUT, new URI(r.getLink(Link.REL_SELF).getHref()));
		ResponseEntity<RevisionResource> rOut = restTemplate.exchange(in, RevisionResource.class);
		Assert.assertTrue(rOut.getStatusCode().is2xxSuccessful());
		
		// retrieve it
		in = new RequestEntity<>(createHeaders(), HttpMethod.GET, rOut.getHeaders().getLocation());
		rOut = restTemplate.exchange(in, RevisionResource.class);
		
		Assert.assertEquals(r.xml, rOut.getBody().xml);
		Assert.assertNotNull(rOut.getBody().getLink("document").getHref());
		return rOut.getBody();
		
	}
	
	public DocumentResource createADocumentResource(ProjectResource forProject) throws URISyntaxException {
		DocumentResource d = new DocumentResource("My Document", "Some name for a document", forProject.getLink(Link.REL_SELF).getHref());
		RequestEntity<DocumentResource> in = new RequestEntity<>(d, createHeaders(), HttpMethod.POST, new URI(documentsUrl));
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
	
	public byte[] getADL(URI uri) {
		RequestEntity<String> in = new RequestEntity<>(createJWTTokenHeaders(jwtToken, null, MediaTypes.ADL_SVG), HttpMethod.GET, uri);
		ResponseEntity<byte[]> dOut = restTemplate.exchange(in, byte[].class);
		return dOut.getBody();
	}

	public ProjectResource createAProjectResource() throws URISyntaxException {
		ProjectResource pIn = new ProjectResource("Test Project 2", "Lorem Ipsum 1", "tp2", "");
		RequestEntity<ProjectResource> re = new RequestEntity<>(pIn, createHeaders(), HttpMethod.POST, new URI(projectUrl));
		
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
