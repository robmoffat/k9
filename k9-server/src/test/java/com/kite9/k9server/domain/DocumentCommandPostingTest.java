package com.kite9.k9server.domain;

import java.net.URI;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;

import com.kite9.k9server.XMLCompare;
import com.kite9.k9server.adl.format.media.MediaTypes;
import com.kite9.k9server.domain.rels.ChangeResourceProcessor;
import com.kite9.k9server.domain.rels.ContentResourceProcessor;
import com.kite9.k9server.resource.DocumentResource;
import com.kite9.k9server.resource.ProjectResource;
import com.kite9.k9server.resource.RevisionResource;
import com.kite9.k9server.security.Hash;

public class DocumentCommandPostingTest extends AbstractLifecycleTest {

	@Test
	public void testCommandPosting() throws Exception {
		ProjectResource pr = createAProjectResource();
		DocumentResource dr  = createADocumentResource(pr);
		RevisionResource rr = createARevisionResource(dr);
		
		URI uri = new URI(dr.getLink(ChangeResourceProcessor.CHANGE_REL).getHref());
		URI revUri = new URI(rr.getLink(ContentResourceProcessor.CONTENT_REL).getHref());
		
		byte[] back1 = postCommand("[{\"type\": \"SetText\", \"fragmentId\": 0, \"fragmentHash\": \"ce8e7037744567a648dccec273d88c604954f114\", \"newText\": \"This is some text\"}]", uri);
		persistInAFile(back1, "revisions", "state1.xml");
		
		byte[] back2 = postCommand("[{\"type\": \"SetAttr\", \"fragmentId\": 0, \"fragmentHash\": \"c31e03d4da676d1d253fdc44b3a87cfae9c958a3\", \"name\": \"style\", \"value\": \"background-color: red; \"}]", uri);
		persistInAFile(back2, "revisions", "state2.xml");
				
		// retrieve the current revision
		String s = dr.getLink("currentRevision").getHref();
		s = s.substring(0, s.indexOf("{?projection}"));
		URI cr = new URI(s);
		RequestEntity<Object> in = new RequestEntity<>(createHeaders(), HttpMethod.GET, cr);
		ResponseEntity<RevisionResource> outR = restTemplate.exchange(in, RevisionResource.class);
		RevisionResource rr2 = outR.getBody();
		
		// now get the non-projection version.
		cr = new URI(rr2.getLink("self").getHref());
		in = new RequestEntity<>(createHeaders(), HttpMethod.GET, cr);
		outR = restTemplate.exchange(in, RevisionResource.class);
		rr2 = outR.getBody();
		
		// make sure expected Xml is set.
		Assert.assertTrue(!rr2.getId().equals(rr.getId()));
		persistInAFile(rr2.xml.getBytes(), "revisions", "state2.1.xml");
		XMLCompare.compareXML(new String(back2), rr2.xml);
				
		// if we perform undo, we should arrive back at the original document.
		String hash = Hash.generateSHA1Hash(new String(back2));
		byte[] back3 = postCommand("[{\"type\": \"Undo\", \"fragmentHash\": \""+hash+"\"}]", uri);
		persistInAFile(back3, "revisions", "state3.xml");
		XMLCompare.compareXML(new String(back1), new String(back3));
		
		// if we perform a redo, we should arrive back at the document after-edit
		hash = Hash.generateSHA1Hash(new String(back1));
		byte[] back4 = postCommand("[{\"type\": \"Redo\", \"fragmentHash\": \""+hash+"\"}]", uri);
		persistInAFile(back4, "revisions", "state4.xml");
		XMLCompare.compareXML(new String(back2), new String(back4));
		
		// we should be able to perform an operation using a previous revision (although this does nothing)
		byte[] back5 = postCommand("[{\"type\": \"ADLReplace\", \"fragmentId\": \"0\", \"approach\": \"ATTRIBUTES\", \"uriStr\": \""+revUri.toString()+"#0\"}]", uri);
		persistInAFile(back5, "revisions", "state5.xml");
		XMLCompare.compareXML(new String(back2), new String(back5));		
	}

	private byte[] postCommand(String commands, URI uri) {
		RequestEntity<byte[]> in = new RequestEntity<>(commands.getBytes(), createJWTTokenHeaders(jwtToken, MediaType.APPLICATION_JSON, MediaTypes.ADL_SVG), HttpMethod.POST, uri);
		ResponseEntity<byte[]> dOut = restTemplate.exchange(in, byte[].class);
		return dOut.getBody();
	}	
}
