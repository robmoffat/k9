package com.kite9.k9server.security;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailMessage;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.kite9.k9server.AbstractAuthenticatedIT;
import com.kite9.k9server.AbstractRestIT;
import com.kite9.k9server.domain.project.Project;
import com.kite9.k9server.domain.user.User;
import com.kite9.k9server.domain.user.UserController;
import com.kite9.k9server.resource.UserResource;
import com.kite9.k9server.web.NotificationResource;

public class RestUserAndSecurityIT extends AbstractAuthenticatedIT {

	@Test
	public void testCreateUserRestAPI() throws URISyntaxException {
		RestTemplate restTemplate = getRestTemplate();
		
		String username = "Kite9TestUser";
		String password = "Elephant";
		String email = "test-user4@example.com";
		
		UserResource uOut = createUser(restTemplate, username, password, email);
		String url = uOut.getLink(Link.REL_SELF).getHref();
		Assert.assertEquals(username, uOut.username);
		Assert.assertNotNull(uOut.api);
		
		// try to create over the top
		try {
			createUser(restTemplate, username, password, email);
		} catch (HttpClientErrorException e) { 
			Assert.assertEquals(HttpStatus.CONFLICT, e.getStatusCode());
		}
		
		// retrieve the user, testing basic authentication
		Resources<UserResource> uOuts = retrieveUserViaBasicAuth(restTemplate, password, email);
		Collection<UserResource> us = uOuts.getContent();
		Assert.assertEquals(1, us.size());
		Assert.assertEquals(username, us.iterator().next().username);
		
		// retrieve the user with the wrong password
		try {
			retrieveUserViaBasicAuth(restTemplate, "blah", email);
		} catch (HttpClientErrorException e) {
			Assert.assertTrue(e.getStatusCode().is4xxClientError());
		}
		
		deleteAndCheckDeleted(restTemplate, url, uOut, UserResource.class);
	}
	
	@Test
	public void testEmailVerification() throws URISyntaxException {		
		List<SimpleMailMessage> messages = storeEmails();
		
		RestTemplate restTemplate = getRestTemplate();

		// first, create a user
		String email = "test-user3@example.com";
		String password = "1234";
		UserResource uOut = createUser(restTemplate, "Kite9TestUser", password, email);
		Assert.assertFalse(uOut.emailVerified);
		
		// make sure that the api can be called to email them
		Link l = uOut.getLink(UserController.VALIDATE_REL);
		String href = l.getHref();
		ResponseEntity<NotificationResource> resp = restTemplate.getForEntity(href, NotificationResource.class);
		Assert.assertEquals("Please check your email for a message from Kite9 Support.", resp.getBody().getMessage());
		
		// check that an email has arrived
		Assert.assertEquals(1, messages.size());
		
		
		// ensure that a wrong email addresss will fail the call
		try {
			String wrongHref = href.replace(email, "blardyblar");
			resp = restTemplate.getForEntity(wrongHref, NotificationResource.class);
			Assert.fail();
		} catch (HttpClientErrorException e) {
			Assert.assertEquals(HttpStatus.BAD_REQUEST, e.getStatusCode());
		}
		
		// next, simulate the approval process
		String messageText = messages.get(0).getText();
		String url = messageText.substring(messageText.indexOf("http"));
		resp = restTemplate.getForEntity(url, NotificationResource.class);
		Assert.assertEquals(HttpStatus.OK, resp.getStatusCode());
		Assert.assertEquals("Email validated", resp.getBody().getMessage());

		// check validation flag now set
		Resources<UserResource> uOuts = retrieveUserViaBasicAuth(restTemplate, password, email);
		Assert.assertEquals(1, uOuts.getContent().size());
		uOut = uOuts.getContent().iterator().next();
		Assert.assertTrue(uOut.emailVerified);
		
		// remove the user
		delete(restTemplate, uOut.getLink(Link.REL_SELF).getHref(), uOut);
		messages.clear();

		// check access is revoked
		try {
			retrieveResource(restTemplate, uOut, url, Void.class);
			Assert.fail();
		} catch (Throwable e) {
		}
	}

	public List<SimpleMailMessage> storeEmails() {
		List<SimpleMailMessage> messages = new ArrayList<>();
		
		Mockito.doAnswer(new Answer<Void>() {

			@Override
			public Void answer(InvocationOnMock invocation) throws Throwable {
				messages.add(invocation.getArgumentAt(0, SimpleMailMessage.class));
				return null;
			}
			
				
		}).when(mailSender).send(Mockito.any(SimpleMailMessage.class));
		return messages;
	}

	protected String generateResponseUrl(String email, String code, String path) {
		return urlBase+"/api/users/"+email+path+( code == null ? "" : "?code="+code);
	}
	
	@Test
	public void testPasswordChange() throws URISyntaxException {
		List<SimpleMailMessage> messages = storeEmails();
		
		RestTemplate restTemplate = getRestTemplate();

		// first, create a user
		String email = "test-user2@example.com";
		String password = "12345";
		UserResource uOut = createUser(restTemplate, "Kite9TestUser2", password, email);

		// ask for a password reset form
		String href = uOut.getLink(UserController.PASSWORD_RESET_REL).getHref();
		ResponseEntity<NotificationResource> pOut = restTemplate.getForEntity(href, NotificationResource.class);
		Assert.assertEquals("Please check your email for a message from Kite9 Support.", pOut.getBody().getMessage());
		
		// ok, try accessing the form using the URL that would have been sent.
		Assert.assertEquals(1, messages.size());
		String messageText = messages.get(0).getText();
		String url = messageText.substring(messageText.indexOf("http"));

		ResponseEntity<String> formOut = restTemplate.getForEntity(url, String.class);
		Assert.assertEquals(HttpStatus.OK, formOut.getStatusCode());
		Assert.assertTrue("Code not set correctly", formOut.getBody().contains(
				"<tr style=\"display: none\"><td>Code:</td><td><input type='text' name='code' value='"+code+"'></td></tr>"));
		
		Assert.assertTrue("Email Not set correctly", formOut.getBody().contains(
				"<tr><td>Email: </td><td>"+email+"</td></tr>"));
		
		String postUrl = generateResponseUrl(email, null, UserController.PASSWORD_RESET_RESPONSE_URL);
		Assert.assertTrue("Action URL Not Set Correctly", formOut.getBody().contains("action='"+postUrl+"'"));
		
		// ok, now try a post of the form, and see if the password can be reset.
		String newPassword = "123456";
		MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
		map.add("password", newPassword);
		map.add("code", code);
		map.add("submit", "Change");
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		HttpEntity<MultiValueMap<String, String>> in = new HttpEntity<MultiValueMap<String,String>>(map, headers);
		ResponseEntity<NotificationResource> cOut = restTemplate.postForEntity(postUrl, in, NotificationResource.class);
		Assert.assertEquals("Password updated", cOut.getBody().getMessage());
		Assert.assertEquals(HttpStatus.OK, cOut.getStatusCode());
		
		// check that the code no longer works.
		try {
			cOut = restTemplate.postForEntity(postUrl, in, NotificationResource.class);
			Assert.fail();
		} catch (HttpClientErrorException e) {
			Assert.assertEquals(HttpStatus.BAD_REQUEST, e.getStatusCode());
		}
		
		// check that we can log in.
		ResponseEntity<String> s = formLogin(restTemplate, email, newPassword);
		Assert.assertEquals(HttpStatus.FOUND, s.getStatusCode());
		Assert.assertEquals(urlBase+"/", s.getHeaders().getLocation().toString());
		
		// check we can't log in with old password
		s = formLogin(restTemplate, email, password);
		Assert.assertEquals(HttpStatus.FOUND, s.getStatusCode());
		Assert.assertEquals(urlBase+"/login?error", s.getHeaders().getLocation().toString());
		
		// delete the user
		delete(restTemplate, uOut.getHeaders().getLocation().toString(), u);

	}
	
	@Test
	public void testFormBasedLogin() throws URISyntaxException {
		RestTemplate restTemplate = getRestTemplate();
		
		// first, create a user
		String email = "test-user1@example.com";
		String password = "1234";
		ResponseEntity<Resource<User>> uOut = createUser(restTemplate, "Kite9TestUser", password, email);
		Assert.assertEquals(HttpStatus.CREATED, uOut.getStatusCode());
		
		// try to access a protected resource, should be automatically redirected to the login page
		ResponseEntity<String> pOut = restTemplate.getForEntity(urlBase+ "/api/projects", String.class);
		Assert.assertTrue(pOut.getBody().contains("<title>Login Page</title>"));
	
		// try posting the login form information, 
		pOut = formLogin(restTemplate, email, password);
		Assert.assertEquals(HttpStatus.FOUND, pOut.getStatusCode());
		Assert.assertEquals(urlBase+"/", pOut.getHeaders().getLocation().toString());
		List<String> cookie = pOut.getHeaders().get("Set-Cookie");
		Assert.assertNotNull(cookie);
		
		// try to create a project with this cookie
		Project pIn = new Project("Test Project", "Lorem Ipsum", "tp2");
		ResponseEntity<Project> projOut = exchangeUsingCookie(restTemplate, urlBase+"/api/projects", cookie.get(0), pIn, HttpMethod.POST, Project.class);
		Assert.assertEquals(HttpStatus.CREATED, projOut.getStatusCode());
		
		// retrieve it again
		ResponseEntity<Project> pGet = exchangeUsingCookie(restTemplate, projOut.getHeaders().getLocation().toString(), cookie.get(0), "", HttpMethod.GET, Project.class);
		Assert.assertEquals("Test Project", pGet.getBody().getTitle());
	
		// try with "wrong" cookie
		try {
			String wrongCookie = "JSESSIONID=248647FE4985967E521D59F1B18C6630; Path=/; HttpOnly";
			pGet = exchangeUsingCookie(restTemplate, projOut.getHeaders().getLocation().toString(), wrongCookie, "", HttpMethod.GET, Project.class);
			Assert.fail();
		} catch (HttpClientErrorException e) {
			Assert.assertTrue(e.getStatusCode().is4xxClientError());
		}
		
		// tidy up: delete project
		delete(restTemplate, projOut.getHeaders().getLocation().toString(), uOut.getBody().getContent());
		
		// remove user
		delete(restTemplate, uOut.getHeaders().getLocation().toString(), uOut.getBody().getContent());
	}
	
	

}
