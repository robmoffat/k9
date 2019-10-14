package com.kite9.k9server.domain;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;

import com.kite9.k9server.adl.format.media.MediaTypes;
import com.kite9.k9server.adl.holder.ADL;
import com.kite9.k9server.command.Command;
import com.kite9.k9server.command.xml.SetText;
import com.kite9.k9server.domain.rels.ContentResourceProcessor;
import com.kite9.k9server.resource.DocumentResource;
import com.kite9.k9server.resource.ProjectResource;
import com.kite9.k9server.resource.RevisionResource;

public class DomainObjectResourceLifecycleTest extends AbstractLifecycleTest {
	
	@Test
	public void testLifeCycle() throws Exception {
		ProjectResource pOut = createAProjectResource();
		pOut = updateAProjectResource(pOut);
		
		DocumentResource dOut = createADocumentResource(pOut);

		RevisionResource rOut = changeTheDocument(dOut);

		dOut = updateADocumentResource(dOut);
		
		// get the latest resource via the revision
		URI uri = new URI(rOut.getLink(ContentResourceProcessor.CONTENT_REL).getHref());
		byte[] input = getADL(uri);
		Assert.assertTrue(new String(input).contains("This is the internal text"));
		
		// get the latest resource via the document
		URI uri2 = new URI(dOut.getLink(ContentResourceProcessor.CONTENT_REL).getHref());
		input = getADL(uri2);
		Assert.assertTrue(new String(input).contains("This is the internal text"));
		
		deleteAndCheckDeleted(restTemplate, pOut.getLink(Link.REL_SELF).getHref(), jwtToken, ProjectResource.class);
	}
	
	public RevisionResource changeTheDocument(DocumentResource dr) throws URISyntaxException {
		SetText st = new SetText("dia", null, "This is the internal text");
		String docUrl = dr.getLink(Link.REL_SELF).getHref();
		RequestEntity<List<Command>> in = new RequestEntity<>(new CommandList(st), createHeaders(), HttpMethod.POST, new URI(docUrl+"/change"));
		ResponseEntity<?> rOut = restTemplate.exchange(in, byte[].class);
		Assert.assertTrue(rOut.getStatusCode().is2xxSuccessful());
		
		// retrieve it
		in = new RequestEntity<>(createHeaders(), HttpMethod.GET, new URI(dr.getLink("currentRevision").expand("").getHref()));
		ResponseEntity<RevisionResource> rOut2 = restTemplate.exchange(in, RevisionResource.class);
		Assert.assertTrue(rOut2.getStatusCode().is2xxSuccessful());
		return rOut2.getBody();
		
	}
	
	public byte[] getADL(URI uri) {
		RequestEntity<String> in = new RequestEntity<>(createJWTTokenHeaders(jwtToken, null, MediaTypes.ADL_SVG), HttpMethod.GET, uri);
		ResponseEntity<byte[]> dOut = restTemplate.exchange(in, byte[].class);
		return dOut.getBody();
	}

}
