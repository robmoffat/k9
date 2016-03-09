package com.kite9.k9server.domain;


import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.client.RestTemplate;

import com.kite9.k9server.Kite9ServerApplication;

@IntegrationTest
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Kite9ServerApplication.class)
@WebAppConfiguration
@ActiveProfiles("dev")
public class RestRoundTripIT {

	RestTemplate restTemplate = new RestTemplate(new SimpleClientHttpRequestFactory());
	
	@Value("${application.url:http://localhost:8080}")
	private String urlBase;
	
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
	public void testDocument() {
		// create a project
		Project pIn = new Project("Test Project", "Lorem Ipsum", "tp1");
		ResponseEntity<Project> pOut = restTemplate.postForEntity(urlBase+"/api/projects", pIn, Project.class);
		
		// create a document on this project
		Project pOutProject = pOut.getBody();
		String url = pOut.getHeaders().getLocation().getPath();
		long projectId = Long.parseLong(url.substring(url.lastIndexOf("/")+1));
		pOutProject.setId(projectId);
		Document d = new Document("Doc Test", "Blah", pOutProject);
		ResponseEntity<Document> dOut = restTemplate.postForEntity(urlBase+"/api/documents", d, Document.class);
	
		// list the documents for a project
		ResponseEntity<Document[]> docList = restTemplate.postForEntity(urlBase+"/api/projects/"+projectId+"/documents", d, Document[].class);
		
		
		restTemplate.delete(dOut.getHeaders().getLocation());
	}
	
	public void checkEquals(Project expected, Project actual) {
		Assert.assertEquals(expected.getTitle(), actual.getTitle());
		Assert.assertEquals(expected.getDescription(), actual.getDescription());
		Assert.assertEquals(expected.getStub(), actual.getStub());
	}
	
}
