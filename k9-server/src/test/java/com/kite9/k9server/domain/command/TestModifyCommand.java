package com.kite9.k9server.domain.command;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.StreamUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kite9.k9server.AbstractAuthenticatedIT;
import com.kite9.k9server.command.Change;
import com.kite9.k9server.command.ModifyCommand;
import com.kite9.k9server.domain.Document;
import com.kite9.k9server.domain.Project;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TestModifyCommand extends AbstractAuthenticatedIT {
	
	protected String commandsUrl = urlBase + "/api/v1/command";
	
	private Resource<Document> d;
	
	@Before
	public void ensureDocument() throws Exception {
		if (d == null) {
			Project pIn = new Project("Test Project 2", "Lorem Ipsum 1", "tp2");
			ResponseEntity<Resource<Project>> project = createAProject(pIn);
			d = createDocumentOld(project.getBody(), "Some Document");
		}
	}
	
	protected Resource<Document> createDocumentUntested(Resource<Project> p, String title) throws URISyntaxException {
		Document d = new Document(title, "Blah", null);
		Resource<Document> rd = new Resource<Document>(d, new Link(p.getLink(Link.REL_SELF).getHref(), "project"));
		HttpHeaders auth = createKite9AuthHeaders(u.getApi());
		RequestEntity<Resource<Document>> in = new RequestEntity<Resource<Document>>(rd, auth, HttpMethod.POST, new URI(documentsUrl));
		ResponseEntity<Resource<Document>> dOut = restTemplate.exchange(in, DOCUMENT_RESOURCE_TYPE);
		return dOut.getBody();
	}
	
	Resource<Document> createDocumentOld(Resource<Project> p, String title) throws JsonProcessingException, URISyntaxException { 
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
		
		ResponseEntity<Resource<Document>> dOut = restTemplate.exchange(in, DOCUMENT_RESOURCE_TYPE);
		return dOut.getBody();
	}
	
	
	
	@Test
	public void testModification() throws Exception {
		String xml = StreamUtils.copyToString(this.getClass().getResourceAsStream("/test_command1.xml"), Charset.forName("UTF-8"));
		ModifyCommand mc = new ModifyCommand(null, u, null, null, xml);
		
		Resource<ModifyCommand> rmc = new Resource<ModifyCommand>(mc, new Link(d.getLink(Link.REL_SELF).getHref(), "document"));

		HttpHeaders auth = createKite9AuthHeaders(u.getApi(), MediaType.APPLICATION_JSON_UTF8, MediaType.APPLICATION_JSON_UTF8);
		RequestEntity<Resource<ModifyCommand>> in = new RequestEntity<Resource<ModifyCommand>>(rmc, auth, HttpMethod.POST, new URI(commandsUrl));
		ResponseEntity<Change> change = restTemplate.exchange(in, Change.class);

		System.out.println("Change: "+change);
	}
	
	
}
