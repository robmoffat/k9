package com.kite9.k9server.docker;

import java.net.URI;
import java.net.URISyntaxException;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.mvc.TypeReferences;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailSender;
import org.springframework.web.client.RestTemplate;

import com.kite9.k9server.domain.Document;
import com.kite9.k9server.domain.Project;
import com.kite9.k9server.domain.User;

/**
 * Makes sure there is a new user available to send REST requests with.
 * 
 * @author robmoffat
 *
 */
public class AbstractAuthenticatedIT extends AbstractRestIT {
	
	@MockBean
	MailSender mailSender;

	protected RestTemplate restTemplate = getRestTemplate();
	protected User u;
	protected String userUrl;
	protected String documentsUrl = urlBase + "/api/documents";
	protected String projectUrl = urlBase + "/api/projects";

	public static TypeReferences.ResourcesType<Resource<Document>> DOCUMENT_RESOURCES_TYPE = new TypeReferences.ResourcesType<Resource<Document>>() {};
	public static TypeReferences.ResourceType<Document> DOCUMENT_RESOURCE_TYPE = new TypeReferences.ResourceType<Document>() {};
	public static TypeReferences.ResourceType<Project> PROJECT_RESOURCE_TYPE = new TypeReferences.ResourceType<Project>() {};
	
	@Before
	public void withUser() throws URISyntaxException {
		ResponseEntity<Resource<User>> userEntity = createUser(restTemplate, "abc1234", "facts", "thing2@example.com");
		u = userEntity.getBody().getContent();
		userUrl = userEntity.getHeaders().getLocation().toString();
	}
	
	@After
	public void removeUser() throws URISyntaxException {
		delete(restTemplate, userUrl, u);
	}

	public ResponseEntity<Resource<Project>> createAProject(Project pIn) throws URISyntaxException {
		RequestEntity<Project> re = new RequestEntity<Project>(pIn, createKite9AuthHeaders(u.getApi()), HttpMethod.POST, new URI(projectUrl));
		ResponseEntity<Resource<Project>> pOut = restTemplate.exchange(re, PROJECT_RESOURCE_TYPE);
		checkEquals(pIn, pOut.getBody().getContent());
		Assert.assertEquals(HttpStatus.CREATED, pOut.getStatusCode());
		return pOut;
	}

	public void checkEquals(Project expected, Project actual) {
		Assert.assertEquals(expected.getTitle(), actual.getTitle());
		Assert.assertEquals(expected.getDescription(), actual.getDescription());
		Assert.assertEquals(expected.getStub(), actual.getStub());
	}
		
}
