package com.kite9.k9server.domain;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;

import com.kite9.k9server.AbstractUserBasedTest;
import com.kite9.k9server.command.Command;
import com.kite9.k9server.command.domain.NewDocument;
import com.kite9.k9server.command.domain.RenameDocument;
import com.kite9.k9server.domain.entity.Repository;
import com.kite9.k9server.resource.DocumentResource;

public abstract class AbstractLifecycleTest extends AbstractUserBasedTest {

	public static final String EMPTY_DOCUMENT = "<svg xmlns=\"http://www.w3.org/2000/svg\" id=\"0\"></svg>";


	public AbstractLifecycleTest() {
		super();
	}

	public DocumentResource createADocumentResource(Repository forProject, String uri) throws URISyntaxException {
		NewDocument nd = new NewDocument();
		nd.description = "Some name for a document";
		nd.title = "test-document.kite9.xml";
		nd.templateUri=uri;
		nd.open = true;
		nd.setSubjectUri(forProject.getLink(IanaLinkRelations.SELF).get().getHref());
		
		RequestEntity<List<Command>> in = new RequestEntity<>(new CommandList(nd), createHeaders(), HttpMethod.POST,getAdminUri());
		ResponseEntity<DocumentResource> dOut = restTemplate.exchange(in, DocumentResource.class);
		Assert.assertEquals(HttpStatus.OK, dOut.getStatusCode());
		return getADocumentResource(new URI(dOut.getBody().getLink(IanaLinkRelations.SELF).get().getHref()));
	}

	public DocumentResource updateADocumentResource(DocumentResource d) throws URISyntaxException {
		RenameDocument u = new RenameDocument();
		u.description = "desc 2";
		u.setSubjectUri(d.getLink(Link.REL_SELF).getHref());
		
		RequestEntity<List<Command>> re = new RequestEntity<>(new CommandList(u), createHeaders(), HttpMethod.POST, getAdminUri());
		ResponseEntity<DocumentResource> dOut = restTemplate.exchange(re, DocumentResource.class);
		Assert.assertTrue(dOut.getStatusCode().is2xxSuccessful());
		return getADocumentResource(new URI(dOut.getBody().getLink(Link.REL_SELF).getHref()));
	}

	public DocumentResource getADocumentResource(URI location) {
		RequestEntity<DocumentResource> in;
		ResponseEntity<DocumentResource> dOut;
		in = new RequestEntity<>(createHeaders(), HttpMethod.GET, location);
		dOut = restTemplate.exchange(in, DocumentResource.class);
		return dOut.getBody();
	}

	
	protected HttpHeaders createHeaders() {
		HttpHeaders out = createTokenHeaders(applicationToken, MediaType.APPLICATION_JSON);
		out.setAccept(Collections.singletonList(MediaTypes.HAL_JSON));
		out.setContentType(MediaType.APPLICATION_JSON);
		return out;	
	}
}