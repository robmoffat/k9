package com.kite9.k9server.domain;

import java.net.URI;
import java.net.URISyntaxException;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;

import com.kite9.k9server.adl.format.media.MediaTypes;
import com.kite9.k9server.domain.document.DocumentController;
import com.kite9.k9server.domain.revision.RevisionController;
import com.kite9.k9server.resource.DocumentResource;
import com.kite9.k9server.resource.ProjectResource;
import com.kite9.k9server.resource.RevisionResource;

public class DomainObjectResourceLifecycleTest extends AbstractLifecycleTest {
	
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
		
		// get the latest resource via the document
		URI uri2 = new URI(dOut.getLink(DocumentController.CONTENT_REL).getHref());
		input = getADL(uri2);
		Assert.assertTrue(new String(input).contains("some new xml"));
		
		deleteAndCheckDeleted(restTemplate, pOut.getLink(Link.REL_SELF).getHref(), jwtToken, ProjectResource.class);
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
	
	public byte[] getADL(URI uri) {
		RequestEntity<String> in = new RequestEntity<>(createJWTTokenHeaders(jwtToken, null, MediaTypes.ADL_SVG), HttpMethod.GET, uri);
		ResponseEntity<byte[]> dOut = restTemplate.exchange(in, byte[].class);
		return dOut.getBody();
	}

}
