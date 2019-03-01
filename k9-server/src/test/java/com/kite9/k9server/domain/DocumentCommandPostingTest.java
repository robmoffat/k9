package com.kite9.k9server.domain;

import java.net.URI;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;

import com.kite9.k9server.adl.format.media.MediaTypes;
import com.kite9.k9server.domain.document.DocumentController;
import com.kite9.k9server.resource.DocumentResource;
import com.kite9.k9server.resource.ProjectResource;
import com.kite9.k9server.resource.RevisionResource;

public class DocumentCommandPostingTest extends AbstractLifecycleTest {

	@Test
	public void testCommandPosting() throws Exception {
		ProjectResource pr = createAProjectResource();
		DocumentResource dr  = createADocumentResource(pr);
		RevisionResource rr = createARevisionResource(dr);
		
		URI uri = new URI(dr.getLink(DocumentController.CHANGE_REL).getHref());
		String back = postCommand("[{\"type\": \"SetText\", \"fragmentId\": 0, \"fragmentHash\": \"ce8e7037744567a648dccec273d88c604954f114\", \"newText\": \"This is some text\"}]", uri);
		
		System.out.println("Document is now: "+back);
		
		back = postCommand("[{\"type\": \"SetAttr\", \"fragmentId\": 0, \"fragmentHash\": \"c31e03d4da676d1d253fdc44b3a87cfae9c958a3\", \"name\": \"style\", \"value\": \"background-color: red; \"}]", uri);
		
		// retrieve the current revision
		URI cr = new URI(dr.getLink("currentRevision").getHref());
		RequestEntity<Object> in = new RequestEntity<>(createHeaders(), HttpMethod.GET, cr);
		ResponseEntity<RevisionResource> outR = restTemplate.exchange(in, RevisionResource.class);
		RevisionResource rr2 = outR.getBody();
		Assert.assertTrue(!rr2.getId().equals(rr.getId()));
		
		// if we perform two undos, we should arrive back at the original document.
		
	}

	private String postCommand(String commands, URI uri) {
		RequestEntity<byte[]> in = new RequestEntity<>(commands.getBytes(), createJWTTokenHeaders(jwtToken, MediaType.APPLICATION_JSON, MediaTypes.ADL_SVG), HttpMethod.POST, uri);
		ResponseEntity<byte[]> dOut = restTemplate.exchange(in, byte[].class);
		return new String(dOut.getBody());
	}	
}
