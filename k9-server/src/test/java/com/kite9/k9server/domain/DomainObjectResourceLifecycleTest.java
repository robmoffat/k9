package com.kite9.k9server.domain;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.hateoas.mvc.TypeReferences;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kite9.k9server.AbstractAuthenticatedIT;
import com.kite9.k9server.domain.Document;
import com.kite9.k9server.domain.Project;

public class DomainObjectResourceLifecycleTest extends AbstractAuthenticatedIT {
	
	protected String documentsUrl = urlBase + "/api/documents";
	protected String projectUrl = urlBase + "/api/projects";

	public static TypeReferences.ResourcesType<Resource<Document>> DOCUMENT_RESOURCES_TYPE = new TypeReferences.ResourcesType<Resource<Document>>() {};
	public static TypeReferences.ResourceType<Document> DOCUMENT_RESOURCE_TYPE = new TypeReferences.ResourceType<Document>() {};
	public static TypeReferences.ResourceType<Project> PROJECT_RESOURCE_TYPE = new TypeReferences.ResourceType<Project>() {};

	
	@Test
	public void testLifeCycle() throws Exception {
		// create a first project
		Project pOut = createAProjectResource();
				
		// create a document on the project
		Document dOut = createADocumentResource(pOut);
		
		
		
		
		
		deleteAndCheckDeleted(restTemplate, dOut.getLink(Link.REL_SELF).getHref(), u, Document.class);
		deleteAndCheckDeleted(restTemplate, pOut.getLink(Link.REL_SELF).getHref(), u, Project.class);
				
	}
	
	public Document createADocumentResource(Project forProject) throws URISyntaxException {
		Document d = new Document("My Document", "Some name for a document", null);
		d.add(new Link(forProject.getLink(Link.REL_SELF).getHref(), "project"));
		RequestEntity<Document> in = new RequestEntity<>(d, createKite9AuthHeaders(u.getApi()), HttpMethod.POST, new URI(documentsUrl));
		ResponseEntity<Document> dOut = restTemplate.exchange(in, Document.class);
		Assert.assertEquals(HttpStatus.CREATED, dOut.getStatusCode());
		checkFieldEquals(d, dOut.getBody());
		return dOut.getBody();
	}

	public Project createAProjectResource() throws URISyntaxException {
		Project pIn = new Project("Test Project 2", "Lorem Ipsum 1", "tp2");
		RequestEntity<Project> re = new RequestEntity<>(pIn, createKite9AuthHeaders(u.getApi()), HttpMethod.POST, new URI(projectUrl));
		
		ResponseEntity<Project> pOut = restTemplate.exchange(re, Project.class);
		checkFieldEquals(pIn, pOut.getBody());
		Assert.assertEquals(HttpStatus.CREATED, pOut.getStatusCode());
		return pOut.getBody();
	}

	public void checkFieldEquals(Project expected, Project actual) {
		Assert.assertEquals(expected.getTitle(), actual.getTitle());
		Assert.assertEquals(expected.getDescription(), actual.getDescription());
		Assert.assertEquals(expected.getStub(), actual.getStub());
	}
	
	public void checkFieldEquals(Document expected, Document actual) {
		Assert.assertEquals(expected.getTitle(), actual.getTitle());
		Assert.assertEquals(expected.getDescription(), actual.getDescription());
		Assert.assertEquals(expected.getDateCreated(), actual.getDateCreated());
		Assert.assertEquals(expected.getProject(), actual.getProject());
		Assert.assertEquals(expected.getCurrentRevision(), actual.getCurrentRevision());
	}
	
	
	@Test
	public void testProject() throws URISyntaxException {
		
		
		// retrieve it again
		RequestEntity<Void> re2 = new RequestEntity<Void>(createKite9AuthHeaders(u.getApi()), HttpMethod.GET, pOut.getHeaders().getLocation());
		ResponseEntity<Resource<Project>> pGet = restTemplate.exchange(re2, PROJECT_RESOURCE_TYPE);
		
		// delete it
		delete(restTemplate, pOut.getHeaders().getLocation().toString(), u);
		
		// make sure it got deleted
		pGet = restTemplate.exchange(re2, PROJECT_RESOURCE_TYPE);
		Assert.assertEquals(HttpStatus.NOT_FOUND, pGet.getStatusCode());
		
	}

	protected ResponseEntity<Document> createDocumentUntested(Resource<Project> p, String title) throws URISyntaxException {
		HttpHeaders auth = createKite9AuthHeaders(u.getApi());
		RequestEntity<Resource<Document>> in = new RequestEntity<Resource<Document>>(rd, auth, HttpMethod.POST, new URI(documentsUrl));
		ResponseEntity<Document> dOut = restTemplate.exchange(in, Document.class);
		return dOut;
	}
	
	 ResponseEntity<Document> createDocumentOld(Resource<Project> p, String title) throws JsonProcessingException, URISyntaxException { 
		String url = p.getLink(Link.REL_SELF).getHref();
		Map<String, Object> requestBody = new HashMap<String, Object>();
		requestBody.put("title", title);
		requestBody.put("description", "Blah");
		requestBody.put("project", url);
		ObjectMapper mapper = new ObjectMapper();
		
		HttpHeaders auth = createKite9AuthHeaders(u.getApi());
		RequestEntity<String> in = new RequestEntity<String>(
				mapper.writeValueAsString(requestBody), 
				auth, 
				HttpMethod.POST, 
				new URI(documentsUrl));
		
		ResponseEntity<Document> dOut = restTemplate.exchange(in, Document.class);
		return dOut;
	}
	
	
	@Test
	public void testDocument() throws RestClientException, IOException, URISyntaxException {
		HttpHeaders auth = createKite9AuthHeaders(u.getApi());
		
		// create a project
		Project pIn = new Project("Test Project", "Lorem Ipsum", "tp1");
		String docTitle = "Doc Test";
		ResponseEntity<Resource<Project>> pOut = createAProject(pIn);				
		ResponseEntity<Document> dOut = createDocumentOld(pOut.getBody(), docTitle);
		Assert.assertEquals(HttpStatus.CREATED, dOut.getStatusCode());

		// check the document belongs to the project
		String projectDocs = pOut.getBody().getLink("documents").getHref();
		RequestEntity<Void> in2 = new RequestEntity<Void>(auth, HttpMethod.GET, new URI(projectDocs));
		ResponseEntity<Resources<Resource<Document>>> docs = restTemplate.exchange(in2, DOCUMENT_RESOURCES_TYPE);
		Assert.assertEquals(1, docs.getBody().getContent().size());
		Resource<Document> first = docs.getBody().getContent().iterator().next();
		Assert.assertEquals("Doc Test", first.getContent().getTitle());

		// check that the project belongs to the document
		String url = pOut.getBody().getLink(Link.REL_SELF).getHref();

		RequestEntity<Void> pIn2 = new RequestEntity<Void>(auth, HttpMethod.GET, new URI(first.getLink("project").getHref()));
		ResponseEntity<Resource<Project>> pOut2 = restTemplate.exchange(pIn2, PROJECT_RESOURCE_TYPE);
 		Assert.assertEquals(url, pOut2.getBody().getLink(Link.REL_SELF).getHref());
				
		// should cascade the delete
		delete(restTemplate, first.getLink(Link.REL_SELF).getHref(), u);
		delete(restTemplate,url, u);
	}
}
