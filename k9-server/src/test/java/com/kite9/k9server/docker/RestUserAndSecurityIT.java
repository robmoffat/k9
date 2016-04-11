package com.kite9.k9server.docker;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.kite9.k9server.domain.User;
import com.kite9.k9server.security.UserController;

public class RestUserAndSecurityIT extends AbstractRestIT {


	@Test
	public void testCreateUserRestAPI() {
		RestTemplate restTemplate = getRestTemplate();
		
		// this is so we don't throw exceptions when errors occur
		restTemplate.setErrorHandler(new SilentErrorHandler());
		
		String username = "Joe Bloggs";
		String password = "Elephant";
		String email = "joe@example.com";
		
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
	}

	
	
	@Test
	public void testEmailVerification() {
		// first, create a user
		RestTemplate restTemplate = getRestTemplate();
		
		// this is so we don't throw exceptions when errors occur
		restTemplate.setErrorHandler(new SilentErrorHandler());

		String email = "mn@example.com";
		String password = "1234";
		ResponseEntity<User> uOut = createUser(restTemplate, "MightyNew", password, email);
		User u = uOut.getBody();
		String apiKey = u.getApi();
		Assert.assertFalse(u.isEmailVerified());
		
		// make sure that the api can be called to email them
		ResponseEntity<String> resp = emailValidateRequest(restTemplate, apiKey);
		Assert.assertEquals("\"Please check your email for a message from Kite9 Support.\"", resp.getBody());
		
		// ensure that a wrong api key will fail the call
		resp = emailValidateRequest(restTemplate, "blardyblar");
		Assert.assertEquals(HttpStatus.UNAUTHORIZED, resp.getStatusCode());
		
		// next, simulate the approval process
		u.setEmail(email);
		String code = UserController.generateValidationCode(u);
		String url = urlBase+"/api"+UserController.EMAIL_VALIDATION_RESPONSE_URL+"?email="+email+"&code="+code;
		resp = restTemplate.getForEntity(url, String.class);
		Assert.assertEquals(HttpStatus.OK, resp.getStatusCode());
		Assert.assertEquals("\"Email validated\"", resp.getBody());

		// check validation flag now set
		uOut = retrieveUserViaBasicAuth(restTemplate, password, email);
		u = uOut.getBody();
		Assert.assertTrue(u.isEmailVerified());

		
	}

}
