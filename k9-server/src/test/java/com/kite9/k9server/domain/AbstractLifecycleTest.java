package com.kite9.k9server.domain;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.Resources;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;

import com.kite9.k9server.AbstractUserBasedTest;
import com.kite9.k9server.command.Command;
import com.kite9.k9server.command.domain.NewDocument;
import com.kite9.k9server.command.domain.NewProject;
import com.kite9.k9server.command.domain.Update;
import com.kite9.k9server.resource.DocumentResource;
import com.kite9.k9server.resource.MemberResource;
import com.kite9.k9server.resource.ProjectResource;
import com.kite9.k9server.resource.RevisionResource;

public abstract class AbstractLifecycleTest extends AbstractUserBasedTest {

	public static final String EMPTY_DOCUMENT = "<svg xmlns=\"http://www.w3.org/2000/svg\" id=\"0\"></svg>";


	public AbstractLifecycleTest() {
		super();
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

	public DocumentResource createADocumentResource(ProjectResource forProject, String uri) throws URISyntaxException {
		NewDocument nd = new NewDocument();
		nd.description = "Some name for a document";
		nd.title = "My Document";
		nd.templateUri=uri;
		
		RequestEntity<List<Command>> in = new RequestEntity<>(new CommandList(nd), createHeaders(), HttpMethod.POST, new URI(forProject.getLink(Link.REL_SELF).getHref()));
		ResponseEntity<DocumentResource> dOut = restTemplate.exchange(in, DocumentResource.class);
		Assert.assertEquals(HttpStatus.OK, dOut.getStatusCode());
		return getADocumentResource(new URI(dOut.getBody().getLink(Link.REL_SELF).getHref()));
	}

	public DocumentResource updateADocumentResource(DocumentResource d) throws URISyntaxException {
		Update u = new Update();
		u.description = "desc 2";
		
		RequestEntity<List<Command>> re = new RequestEntity<>(new CommandList(u), createHeaders(), HttpMethod.POST, new URI(d.getLink(Link.REL_SELF).getHref()));
		ResponseEntity<DocumentResource> dOut = restTemplate.exchange(re, DocumentResource.class);
		Assert.assertTrue(dOut.getStatusCode().is2xxSuccessful());
		return getADocumentResource(new URI(dOut.getBody().getLink(Link.REL_SELF).getHref()));
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
		NewProject np = new NewProject();
		np.title = "Test Project 2";
		np.description = "Lorem Ipsum 1";
		np.stub = "tp"+stubNumber;
		RequestEntity<List<Command>> re = new RequestEntity<>(new CommandList(np), createHeaders(), HttpMethod.POST, new URI(userUrl));
		
		ResponseEntity<ProjectResource> pOut = restTemplate.exchange(re, ProjectResource.class);
		Assert.assertEquals(HttpStatus.OK, pOut.getStatusCode());
		return getAProjectResource(new URI(pOut.getBody().getLink(Link.REL_SELF).getHref()));
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
		Update u = new Update();
		u.description = "desc 2";
		
		RequestEntity<List<Command>> re = new RequestEntity<>(new CommandList(u), createHeaders(), HttpMethod.POST, new URI(pIn.getLink(Link.REL_SELF).getHref()));
		ResponseEntity<ProjectResource> pOut = restTemplate.exchange(re, ProjectResource.class);
		Assert.assertTrue(pOut.getStatusCode().is2xxSuccessful());
		return getAProjectResource(new URI(pOut.getBody().getLink(Link.REL_SELF).getHref()));
	}


	protected HttpHeaders createHeaders() {
		HttpHeaders out = createJWTTokenHeaders(jwtToken, MediaType.APPLICATION_JSON);
		out.setAccept(Collections.singletonList(MediaTypes.HAL_JSON));
		out.setContentType(MediaType.APPLICATION_JSON);
		return out;	
	}
	
	public void delete(URI url) throws URISyntaxException {
		deleteViaJwt(restTemplate, url.toString(), jwtToken);
	}
}