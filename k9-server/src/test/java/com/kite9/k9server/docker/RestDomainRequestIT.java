package com.kite9.k9server.docker;


import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.kite9.k9server.domain.Document;
import com.kite9.k9server.domain.Project;
import com.kite9.k9server.domain.User;

public class RestDomainRequestIT extends AbstractRestIT {
	
	@Test
	public void testProject() {
		// setup the template
		RestTemplate restTemplate = getRestTemplate();

		// create a user
		User u = createUser(restTemplate, "abc1234", "facts", "thing2@example.com").getBody();

		// create a first project
		String url = urlBase + "/api/projects";
		Project pIn = new Project("Test Project 2", "Lorem Ipsum 1", "tp2");
		ResponseEntity<Project> pOut = postAsTestUser(restTemplate, pIn, Project.class, url, u);
		checkEquals(pIn, pOut.getBody());

		// retrieve it again
		ResponseEntity<Project> pGet = retrieveObjectViaApiAuth(restTemplate, u, pOut.getHeaders().getLocation().toString(), Project.class);
		checkEquals(pIn, pGet.getBody());
		
		// delete it
		deleteViaApiAuth(restTemplate, pOut.getHeaders().getLocation().toString(), u);
		
		// make sure it got deleted
		pGet = retrieveObjectViaApiAuth(restTemplate, u, pOut.getHeaders().getLocation().toString(), Project.class);
		Assert.assertEquals(HttpStatus.NOT_FOUND, pGet.getStatusCode());
		
	}

	@Test
	public void testDocument() throws RestClientException, IOException {
		// setup the template
		RestTemplate restTemplate = getRestTemplate();
		
		// create a user
		User u = createUser(restTemplate, "abc123", "facts", "thing@example.com").getBody();
		
		// create a project
		Project pIn = new Project("Test Project", "Lorem Ipsum", "tp1");
		ResponseEntity<Project> pOut = postAsTestUser(restTemplate, pIn, Project.class, urlBase + "/api/projects", u);
		Assert.assertEquals(HttpStatus.CREATED, pOut.getStatusCode());
		

		// create a document on this project
		String url = pOut.getHeaders().getLocation().toString();
		Map<String, Object> requestBody = new HashMap<String, Object>();
		requestBody.put("title", "Doc Test");
		requestBody.put("description", "Blah");
		requestBody.put("project", url);
		HttpEntity<String> in = createKite9AuthHeaders(u.getApi(), mapper.writeValueAsString(requestBody));
		ResponseEntity<Document> dOut = restTemplate.exchange(urlBase + "/api/documents", HttpMethod.POST, 
				in, Document.class);
		Assert.assertEquals(HttpStatus.CREATED, dOut.getStatusCode());

		// check the document belongs to the project
		ResponseEntity<String> stringList = retrieveObjectViaApiAuth(restTemplate, u, url+"/documents", String.class);
		Assert.assertTrue(stringList.getBody().contains("\"title\" : \"Doc Test\""));
				
		// should cascade the delete
		restTemplate.delete(url);
	}
	
	public void checkEquals(Project expected, Project actual) {
		Assert.assertEquals(expected.getTitle(), actual.getTitle());
		Assert.assertEquals(expected.getDescription(), actual.getDescription());
		Assert.assertEquals(expected.getStub(), actual.getStub());
	}
}
