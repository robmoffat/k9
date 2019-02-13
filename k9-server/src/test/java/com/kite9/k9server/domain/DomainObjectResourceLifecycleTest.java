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

		
		// get the latest resource via the document, in input form
		URI uri = new URI(rOut.getLink(RevisionController.CONTENT_REL).getHref());
		byte[] input = getADL(uri);
		Assert.assertEquals("some new xml", new String(input));
		
		// get the latest resource via the document, in output form
		byte[] output = getSVG(uri);
		Assert.assertEquals("renderedXML", new String(output));
		
		// deleteAndCheckDeleted(restTemplate, rOut.getLink(Link.REL_SELF).getHref(), u, RevisionResource.class);
		deleteAndCheckDeleted(restTemplate, dOut.getLink(Link.REL_SELF).getHref(), u, DocumentResource.class);
		deleteAndCheckDeleted(restTemplate, pOut.getLink(Link.REL_SELF).getHref(), u, ProjectResource.class);
	}
	
	public RevisionResource createARevisionResource(DocumentResource forDocument) throws URISyntaxException {
		String doc = forDocument.getLink(Link.REL_SELF).getHref();
		RevisionResource r = new RevisionResource(doc, "someXML", "abc123", new Date(), userUrl, "renderedXML", null, null);  
		RequestEntity<RevisionResource> in = new RequestEntity<>(r, createKite9AuthHeaders(u.api), HttpMethod.POST, new URI(revisionsUrl));
		ResponseEntity<RevisionResource> rOut = restTemplate.exchange(in, RevisionResource.class);
		Assert.assertEquals(HttpStatus.CREATED, rOut.getStatusCode());
		Assert.assertEquals("someXML", rOut.getBody().inputXml);
		Assert.assertEquals("renderedXML", rOut.getBody().outputXml);
		Assert.assertNotNull(rOut.getBody().getLink("document").getHref());
		return rOut.getBody();
		
	}
	
	public RevisionResource updateARevisionResource(RevisionResource r) throws URISyntaxException {
		r.inputXml = "some new xml";
		RequestEntity<RevisionResource> in = new RequestEntity<>(r, createKite9AuthHeaders(u.api), HttpMethod.PUT, new URI(r.getLink(Link.REL_SELF).getHref()));
		ResponseEntity<RevisionResource> rOut = restTemplate.exchange(in, RevisionResource.class);
		Assert.assertEquals(HttpStatus.OK, rOut.getStatusCode());
		Assert.assertEquals("some new xml", rOut.getBody().inputXml);
		Assert.assertEquals("renderedXML", rOut.getBody().outputXml);
		Assert.assertNotNull(rOut.getBody().getLink("document").getHref());
		return rOut.getBody();
		
	}
	
	public DocumentResource createADocumentResource(ProjectResource forProject) throws URISyntaxException {
		DocumentResource d = new DocumentResource("My Document", "Some name for a document", forProject.getLink(Link.REL_SELF).getHref());
		RequestEntity<DocumentResource> in = new RequestEntity<>(d, createKite9AuthHeaders(u.api), HttpMethod.POST, new URI(documentsUrl));
		ResponseEntity<DocumentResource> dOut = restTemplate.exchange(in, DocumentResource.class);
		Assert.assertEquals(HttpStatus.CREATED, dOut.getStatusCode());
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
		RequestEntity<DocumentResource> in = new RequestEntity<>(changed, createKite9AuthHeaders(u.api), HttpMethod.PUT, self);
		ResponseEntity<DocumentResource> dOut = restTemplate.exchange(in, DocumentResource.class);
		Assert.assertEquals(HttpStatus.OK, dOut.getStatusCode());
		Assert.assertEquals(d.description, dOut.getBody().description);
		return dOut.getBody();
	}
	
	public byte[] getADL(URI uri) {
		RequestEntity<String> in = new RequestEntity<>(createKite9AuthHeaders(u.api, null, MediaTypes.ADL_SVG), HttpMethod.GET, uri);
		ResponseEntity<byte[]> dOut = restTemplate.exchange(in, byte[].class);
		return dOut.getBody();
	}
	
	public byte[] getSVG(URI uri) {
		RequestEntity<String> in = new RequestEntity<>(createKite9AuthHeaders(u.api, null, MediaTypes.SVG), HttpMethod.GET, uri);
		ResponseEntity<byte[]> dOut = restTemplate.exchange(in, byte[].class);
		return dOut.getBody();
	}

	public ProjectResource createAProjectResource() throws URISyntaxException {
		ProjectResource pIn = new ProjectResource("Test Project 2", "Lorem Ipsum 1", "tp2", "");
		RequestEntity<ProjectResource> re = new RequestEntity<>(pIn, createKite9AuthHeaders("absc"), HttpMethod.POST, new URI(projectUrl));
		
		ResponseEntity<ProjectResource> pOut = restTemplate.exchange(re, ProjectResource.class);
		Assert.assertEquals(pIn, pOut.getBody());
		Assert.assertEquals(HttpStatus.CREATED, pOut.getStatusCode());
		return pOut.getBody();
	}

	public ProjectResource updateAProjectResource(ProjectResource pIn) throws URISyntaxException {
		pIn.description = "desc 2";
		RequestEntity<ProjectResource> re = new RequestEntity<>(pIn, createKite9AuthHeaders(u.api), HttpMethod.PUT, new URI(pIn.getLink(Link.REL_SELF).getHref()));
		ResponseEntity<ProjectResource> pOut = restTemplate.exchange(re, ProjectResource.class);
		Assert.assertEquals(pIn, pOut.getBody());
		Assert.assertEquals(HttpStatus.OK, pOut.getStatusCode());
		return pOut.getBody();
	}
	
	
}
