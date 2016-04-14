package com.kite9.k9server.docker;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
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
import com.kite9.k9server.security.UserController;

public class RestUserAndSecurityIT extends AbstractRestIT {


	@Test
	public void testCreateUserRestAPI() {
		RestTemplate restTemplate = getRestTemplate();
		
		// this is so we don't throw exceptions when errors occur
		restTemplate.setErrorHandler(new SilentErrorHandler());
		
		String username = "Kite9TestUser";
		String password = "Elephant";
		String email = "test-user4@example.com";
		
		ResponseEntity<User> uOut = createUser(restTemplate, username, password, email);
		User u = uOut.getBody();
		
		Assert.assertEquals(HttpStatus.OK, uOut.getStatusCode());
		Assert.assertEquals(username, u.getUsername());
		Assert.assertNotNull(u.getApi());
		
		// try to create over the top
		uOut = createUser(restTemplate, username, password, email);
		Assert.assertEquals(HttpStatus.CONFLICT, uOut.getStatusCode());
		
		// retrieve the user, testing basic authentication
		uOut = retrieveUserViaBasicAuth(restTemplate, password, email);
		u = uOut.getBody();
		Assert.assertEquals(username, u.getUsername());
		
		// retrieve the user with the wrong password
		uOut = retrieveUserViaBasicAuth(restTemplate, "blah", email);
		Assert.assertEquals(HttpStatus.UNAUTHORIZED, uOut.getStatusCode());
		
		// remove the user
		//restTemplate.delete(url);
	}

	
	
	@Test
	public void testEmailVerification() {
		// this is so we don't throw exceptions when errors occur
		RestTemplate restTemplate = getRestTemplate();
		restTemplate.setErrorHandler(new SilentErrorHandler());

		// first, create a user
		String email = "test-user3@example.com";
		String password = "1234";
		ResponseEntity<User> uOut = createUser(restTemplate, "Kite9TestUser", password, email);
		User u = uOut.getBody();
		Assert.assertFalse(u.isEmailVerified());
		
		// make sure that the api can be called to email them
		ResponseEntity<String> resp = emailValidateRequest(restTemplate, email);
		Assert.assertEquals("\"Please check your email for a message from Kite9 Support.\"", resp.getBody());
		
		// ensure that a wrong api key will fail the call
		resp = emailValidateRequest(restTemplate, "blardyblar");
		Assert.assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
		
		// next, simulate the approval process
		u.setEmail(email);
		String code = UserController.generateValidationCode(u, UserController.EMAIL_VALIDATION_RESPONSE_URL);
		String url = generateResponseUrl(email, code, "/api"+UserController.EMAIL_VALIDATION_RESPONSE_URL);
		resp = restTemplate.getForEntity(url, String.class);
		Assert.assertEquals(HttpStatus.OK, resp.getStatusCode());
		Assert.assertEquals("\"Email validated\"", resp.getBody());

		// check validation flag now set
		uOut = retrieveUserViaBasicAuth(restTemplate, password, email);
		u = uOut.getBody();
		Assert.assertTrue(u.isEmailVerified());
		
		// remove the user
		//restTemplate.delete(uOut.getHeaders().getLocation());
	}

	protected String generateResponseUrl(String email, String code, String path) {
		return urlBase+path+"?email="+email+"&code="+code;
	}
	
	@Test
	public void testPasswordChange() {
		// this is so we don't throw exceptions when errors occur
		RestTemplate restTemplate = getRestTemplate();
		restTemplate.setErrorHandler(new SilentErrorHandler());

		// first, create a user
		String email = "test-user2@example.com";
		String password = "12345";
		ResponseEntity<User> uOut = createUser(restTemplate, "Kite9TestUser2", password, email);
		User u = uOut.getBody();
		Assert.assertEquals(HttpStatus.OK, uOut.getStatusCode());

		// ask for a password reset form
		ResponseEntity<String> pOut = restTemplate.getForEntity(urlBase+"/api"+UserController.PASSWORD_RESET_REQUEST_URL+"?email="+email, String.class);
		Assert.assertEquals("\"Please check your email for a message from Kite9 Support.\"", pOut.getBody());
		
		// ok, try accessing the form using the URL that would have been sent.
		String code = UserController.generateValidationCode(u, UserController.PASSWORD_RESET_FORM_URL);
		String url = generateResponseUrl(email, code, UserController.PASSWORD_RESET_FORM_URL);
		ResponseEntity<String> formOut = restTemplate.getForEntity(url, String.class);
		Assert.assertEquals(HttpStatus.OK, formOut.getStatusCode());
		Assert.assertTrue("Code not set correctly", formOut.getBody().contains(
				"<tr style=\"display: none\"><td>Code:</td><td><input type='text' name='code' value='"+code+"'></td></tr>"));
		
		Assert.assertTrue("Email Not set correctly", formOut.getBody().contains(
				"<tr><td>Email:</td><td><input type='text' name='email' value='"+email+"'></td></tr>"));
		
		// ok, now try a post of the form, and see if the password can be reset.
		String newPassword = "123456";
		MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
		map.add("email", email);
		map.add("password", newPassword);
		map.add("code", code);
		map.add("submit", "Change");
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		HttpEntity<MultiValueMap<String, String>> in = new HttpEntity<MultiValueMap<String,String>>(map, headers);
		ResponseEntity<String> cOut = restTemplate.postForEntity(urlBase+"/api"+UserController.PASSWORD_RESET_RESPONSE_URL, in, String.class);
		Assert.assertEquals("\"Password updated\"", cOut.getBody());
		Assert.assertEquals(HttpStatus.OK, cOut.getStatusCode());
		
		// check that the code no longer works.
		cOut = restTemplate.postForEntity(urlBase+"/api"+UserController.PASSWORD_RESET_RESPONSE_URL, in, String.class);
		Assert.assertEquals(HttpStatus.BAD_REQUEST, cOut.getStatusCode());
		
		// check that we can log in.
		ResponseEntity<String> s = formLogin(restTemplate, email, newPassword);
		Assert.assertEquals(HttpStatus.FOUND, s.getStatusCode());
		Assert.assertEquals(urlBase+"/", s.getHeaders().getLocation().toString());
		
		// check we can't log in with old password
		s = formLogin(restTemplate, email, password);
		Assert.assertEquals(HttpStatus.FOUND, s.getStatusCode());
		Assert.assertEquals(urlBase+"/login?error", s.getHeaders().getLocation().toString());

	}
	
	@Test
	public void testFormBasedLogin() {
		// this is so we don't throw exceptions when errors occur
		RestTemplate restTemplate = getRestTemplate();
		restTemplate.setErrorHandler(new SilentErrorHandler());
		
		// first, create a user
		String email = "test-user1@example.com";
		String password = "1234";
		ResponseEntity<User> uOut = createUser(restTemplate, "Kite9TestUser", password, email);
		Assert.assertEquals(HttpStatus.OK, uOut.getStatusCode());
		
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
		
	}
	
	

}
