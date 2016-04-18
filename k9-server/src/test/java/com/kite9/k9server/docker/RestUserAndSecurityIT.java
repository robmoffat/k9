package com.kite9.k9server.docker;

import java.net.URISyntaxException;
import java.util.Collection;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.kite9.k9server.domain.Project;
import com.kite9.k9server.domain.User;
import com.kite9.k9server.security.user_repo.UserController;
import com.kite9.k9server.web.NotificationResource;

public class RestUserAndSecurityIT extends AbstractRestIT {


	@Test
	public void testCreateUserRestAPI() throws URISyntaxException {
		RestTemplate restTemplate = getRestTemplate();
		
		String username = "Kite9TestUser";
		String password = "Elephant";
		String email = "test-user4@example.com";
		
		ResponseEntity<Resource<User>> uOut = createUser(restTemplate, username, password, email);
		User u = uOut.getBody().getContent();
		String url = uOut.getHeaders().getLocation().toString();
		
		Assert.assertEquals(HttpStatus.CREATED, uOut.getStatusCode());
		Assert.assertEquals(username, u.getUsername());
		Assert.assertNotNull(u.getApi());
		
		// try to create over the top
		uOut = createUser(restTemplate, username, password, email);
		Assert.assertEquals(HttpStatus.CONFLICT, uOut.getStatusCode());
		
		// retrieve the user, testing basic authentication
		ResponseEntity<Resources<User>> uOuts = retrieveUserViaBasicAuth(restTemplate, password, email);
		Collection<User> us = uOuts.getBody().getContent();
		Assert.assertEquals(1, us.size());
		Assert.assertEquals(username, us.iterator().next().getUsername());
		
		// retrieve the user with the wrong password
		uOuts = retrieveUserViaBasicAuth(restTemplate, "blah", email);
		Assert.assertTrue(uOut.getStatusCode().is4xxClientError());
		
		// remove the user
		delete(restTemplate, url, u);
		
		// check access is revoked
		ResponseEntity<Void> sOut = retrieveObjectViaApiAuth(restTemplate, u, url, Void.class);
		Assert.assertEquals(HttpStatus.UNAUTHORIZED, sOut.getStatusCode());

	}
	
	@Test
	public void testEmailVerification() throws URISyntaxException {
		RestTemplate restTemplate = getRestTemplate();

		// first, create a user
		String email = "test-user3@example.com";
		String password = "1234";
		ResponseEntity<Resource<User>> uOut = createUser(restTemplate, "Kite9TestUser", password, email);
		User u = uOut.getBody().getContent();
		Assert.assertFalse(u.isEmailVerified());
		
		// make sure that the api can be called to email them
		Resource<User> resource = uOut.getBody();
		Link l = resource.getLink(UserController.VALIDATE_REL);
		String href = l.getHref();
		ResponseEntity<NotificationResource> resp = restTemplate.getForEntity(href, NotificationResource.class);
		Assert.assertEquals("Please check your email for a message from Kite9 Support.", resp.getBody().getMessage());
		
		// ensure that a wrong email addresss will fail the call
		String wrongHref = href.replace(email, "blardyblar");
		resp = restTemplate.getForEntity(wrongHref, NotificationResource.class);
		Assert.assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
		
		// next, simulate the approval process
		u.setEmail(email);
		String code = UserController.generateValidationCode(u, UserController.EMAIL_VALIDATION_RESPONSE_URL);
		String url = generateResponseUrl(email, code, UserController.EMAIL_VALIDATION_RESPONSE_URL);
		resp = restTemplate.getForEntity(url, NotificationResource.class);
		Assert.assertEquals(HttpStatus.OK, resp.getStatusCode());
		Assert.assertEquals("Email validated", resp.getBody().getMessage());

		// check validation flag now set
		ResponseEntity<Resources<User>> uOuts = retrieveUserViaBasicAuth(restTemplate, password, email);
		Assert.assertEquals(HttpStatus.OK, uOuts.getStatusCode());
		Assert.assertEquals(1, uOuts.getBody().getContent().size());
		u = uOuts.getBody().getContent().iterator().next();
		Assert.assertTrue(u.isEmailVerified());
		
		// remove the user
		delete(restTemplate, uOut.getHeaders().getLocation().toString(), u);

		// check access is revoked
		ResponseEntity<Void> sOut = retrieveObjectViaApiAuth(restTemplate, u, url, Void.class);
		Assert.assertEquals(HttpStatus.UNAUTHORIZED, sOut.getStatusCode());
	}

	protected String generateResponseUrl(String email, String code, String path) {
		return urlBase+"/api/users/"+email+path+( code == null ? "" : "?code="+code);
	}
	
	@Test
	public void testPasswordChange() throws URISyntaxException {
		RestTemplate restTemplate = getRestTemplate();

		// first, create a user
		String email = "test-user2@example.com";
		String password = "12345";
		ResponseEntity<Resource<User>> uOut = createUser(restTemplate, "Kite9TestUser2", password, email);
		Assert.assertEquals(HttpStatus.CREATED, uOut.getStatusCode());
		User u = uOut.getBody().getContent();

		// ask for a password reset form
		String href = uOut.getBody().getLink(UserController.PASSWORD_RESET_REL).getHref();
		ResponseEntity<NotificationResource> pOut = restTemplate.getForEntity(href, NotificationResource.class);
		Assert.assertEquals("Please check your email for a message from Kite9 Support.", pOut.getBody().getMessage());
		
		// ok, try accessing the form using the URL that would have been sent.
		String code = UserController.generateValidationCode(u, UserController.PASSWORD_RESET_RESPONSE_URL);
		String url = generateResponseUrl(email, code, UserController.PASSWORD_RESET_FORM_URL);
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
		cOut = restTemplate.postForEntity(postUrl, in, NotificationResource.class);
		Assert.assertEquals(HttpStatus.BAD_REQUEST, cOut.getStatusCode());
		
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
		String wrongCookie = "JSESSIONID=248647FE4985967E521D59F1B18C6630; Path=/; HttpOnly";
		pGet = exchangeUsingCookie(restTemplate, projOut.getHeaders().getLocation().toString(), wrongCookie, "", HttpMethod.GET, Project.class);
		Assert.assertTrue(pGet.getStatusCode().is4xxClientError());
		
		// tidy up: delete project
		delete(restTemplate, projOut.getHeaders().getLocation().toString(), uOut.getBody().getContent());
		
		// remove user
		delete(restTemplate, uOut.getHeaders().getLocation().toString(), uOut.getBody().getContent());
	}
	
	

}
