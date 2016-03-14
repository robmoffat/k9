package com.kite9.k9server.docker;


import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kite9.k9server.domain.Document;
import com.kite9.k9server.domain.Project;

public class RestRoundTripIT {
	
	ObjectMapper mapper = new ObjectMapper();
	RestTemplate restTemplate = new RestTemplate(new SimpleClientHttpRequestFactory());
	
	private String urlBase = "http://" + DockerInspector.getImageRunningContainerIPAddress("com.kite9/k9-server") + ":8080";
	
	@Test
	public void testProject() {
		String url = urlBase + "/api/projects";
		Project pIn = new Project("Test Project", "Lorem Ipsum", "tp1");
		ResponseEntity<Project> pOut = restTemplate.postForEntity(url, pIn, Project.class);
		checkEquals(pIn, pOut.getBody());

		// retrieve it again
		Project pGet = restTemplate.getForObject(pOut.getHeaders().getLocation(), Project.class);
		checkEquals(pIn, pGet);
		
		// delete it
		restTemplate.delete(pOut.getHeaders().getLocation());
	}
	
	@Test
	public void testDocument() throws RestClientException, JsonProcessingException {
		// create a project
		Project pIn = new Project("Test Project", "Lorem Ipsum", "tp1");
		ResponseEntity<Project> pOut = restTemplate.postForEntity(urlBase + "/api/projects", pIn, Project.class);
		String url = pOut.getHeaders().getLocation().getPath();

		// create a document on this project
		Map<String, Object> requestBody = new HashMap<String, Object>();
		requestBody.put("title", "Doc Test");
		requestBody.put("description", "Blah");
		requestBody.put("project", url);

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.setContentType(MediaType.APPLICATION_JSON);

		ResponseEntity<Document> dOut = restTemplate.exchange(urlBase + "/api/documents", HttpMethod.POST, 
				new HttpEntity<String>(mapper.writeValueAsString(requestBody), requestHeaders),
				Document.class);

		// list the documents for a project
		ResponseEntity<Document[]> docList = restTemplate.getForEntity(url + "/documents", Document[].class);

		// should cascade the delete
		restTemplate.delete(url);
	}
	
	public void checkEquals(Project expected, Project actual) {
		Assert.assertEquals(expected.getTitle(), actual.getTitle());
		Assert.assertEquals(expected.getDescription(), actual.getDescription());
		Assert.assertEquals(expected.getStub(), actual.getStub());
	}


}
