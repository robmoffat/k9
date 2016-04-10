package com.kite9.k9server.docker;


import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.Resources;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;

import com.kite9.k9server.domain.Document;
import com.kite9.k9server.domain.Project;

public class RestRoundTripIT extends AbstractRestIT {
	
	@Test
	public void testProject() {
		String url = urlBase + "/api/projects";
		Project pIn = new Project("Test Project", "Lorem Ipsum", "tp1");
		ResponseEntity<Project> pOut = getRestTemplate().postForEntity(url, pIn, Project.class);
		checkEquals(pIn, pOut.getBody());

		// retrieve it again
		Project pGet = getRestTemplate().getForObject(pOut.getHeaders().getLocation(), Project.class);
		checkEquals(pIn, pGet);
		
		// delete it
		getRestTemplate().delete(pOut.getHeaders().getLocation());
	}
	
	@Test
	public void testDocument() throws RestClientException, IOException {
		// create a project
		Project pIn = new Project("Test Project", "Lorem Ipsum", "tp1");
		ResponseEntity<Project> pOut = getRestTemplate().postForEntity(urlBase + "/api/projects", pIn, Project.class);
		String url = pOut.getHeaders().getLocation().toString();

		// create a document on this project
		Map<String, Object> requestBody = new HashMap<String, Object>();
		requestBody.put("title", "Doc Test");
		requestBody.put("description", "Blah");
		requestBody.put("project", url);

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.setContentType(MediaType.APPLICATION_JSON);

		ResponseEntity<Document> dOut = getRestTemplate().exchange(urlBase + "/api/documents", HttpMethod.POST, 
				new HttpEntity<String>(mapper.writeValueAsString(requestBody), requestHeaders),
				Document.class);

		// list the documents for a project
		ParameterizedTypeReference<Resources<Document>> pt = new ParameterizedTypeReference<Resources<Document>>() {
		};
		ResponseEntity<Resources<Document>> docList = getRestTemplate().exchange(url + "/documents", HttpMethod.GET, null, pt);

		// should cascade the delete
		getRestTemplate().delete(url);
	}
	
	public void checkEquals(Project expected, Project actual) {
		Assert.assertEquals(expected.getTitle(), actual.getTitle());
		Assert.assertEquals(expected.getDescription(), actual.getDescription());
		Assert.assertEquals(expected.getStub(), actual.getStub());
	}


}
