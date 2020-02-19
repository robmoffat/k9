package com.kite9.k9server.domain;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.kohsuke.github.GitHub;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;

import com.kite9.k9server.AbstractRestIT.CommandList;
import com.kite9.k9server.adl.format.media.Kite9MediaTypes;
import com.kite9.k9server.command.Command;
import com.kite9.k9server.command.domain.AddMembers;
import com.kite9.k9server.command.xml.SetText;
import com.kite9.k9server.domain.entity.EntityController;
import com.kite9.k9server.domain.entity.User;
import com.kite9.k9server.domain.links.ContentResourceProcessor;
import com.kite9.k9server.domain.permission.Member;
import com.kite9.k9server.resource.DocumentResource;
import com.kite9.k9server.resource.MemberResource;
import com.kite9.k9server.resource.ProjectResource;
import com.kite9.k9server.resource.RevisionResource;
import com.kite9.k9server.resource.UserResource;

public class DomainObjectResourceLifecycleTest extends AbstractLifecycleTest {
	
	@Autowired
	EntityController entities;
	
	@Test
	public void testLifeCycle() throws Exception {
		User ur = entities.getHomePage(null);
		
		/*
		
		ProjectResource pOut = createAProjectResource();
		pOut = updateAProjectResource(pOut);
		
		DocumentResource dOut = createADocumentResource(pOut, "http://localhost:"+port+"/public/templates/basic.xml");

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
		
		// add a user to the project
		pOut = addAMemberResource(pOut);
		
	
		deleteAndCheckDeleted(restTemplate, pOut.getLink(Link.REL_SELF).getHref(), jwtToken, ProjectResource.class);*/
	}
	
	@SuppressWarnings("unchecked")
	private ProjectResource addAMemberResource(ProjectResource pIn) throws URISyntaxException {
		// TODO Auto-generated method stub
		AddMembers am = new AddMembers();
		am.emailAddresses =  "jim@doesntexist.com, john@madeup.com";
		am.setSubjectUri(pIn.localId);
		RequestEntity<List<Command>> re = new RequestEntity<>(new CommandList(am), createHeaders(), HttpMethod.POST, getAdminUri());
		ResponseEntity<ProjectResource> pOut = restTemplate.exchange(re, ProjectResource.class);
		
		ProjectResource pr = getAProjectResource(new URI(pOut.getBody().getLink(Link.REL_SELF).getHref()));
		
		Assert.assertEquals(3, ((Collection<Member>) pr._embedded.get("members")).size());
		return pr;
	}

	public RevisionResource changeTheDocument(DocumentResource dr) throws URISyntaxException {
		SetText st = new SetText("dia", null, "This is the internal text");
		String docUrl = dr.getLink(ContentResourceProcessor.CONTENT_REL).getHref();
		RequestEntity<List<Command>> in = new RequestEntity<>(new CommandList(st), createHeaders(), HttpMethod.POST, new URI(docUrl));
		ResponseEntity<?> rOut = restTemplate.exchange(in, byte[].class);
		Assert.assertTrue(rOut.getStatusCode().is2xxSuccessful());
		
		// retrieve it
		in = new RequestEntity<>(createHeaders(), HttpMethod.GET, new URI(dr.getLink("currentRevision").expand("").getHref()));
		ResponseEntity<RevisionResource> rOut2 = restTemplate.exchange(in, RevisionResource.class);
		Assert.assertTrue(rOut2.getStatusCode().is2xxSuccessful());
		return rOut2.getBody();
		
	}
	
	public byte[] getADL(URI uri) {
		RequestEntity<String> in = new RequestEntity<>(createJWTTokenHeaders(jwtToken, null, Kite9MediaTypes.ADL_SVG), HttpMethod.GET, uri);
		ResponseEntity<byte[]> dOut = restTemplate.exchange(in, byte[].class);
		return dOut.getBody();
	}

}
