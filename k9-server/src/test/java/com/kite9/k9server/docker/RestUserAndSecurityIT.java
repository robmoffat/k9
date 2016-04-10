package com.kite9.k9server.docker;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;


import org.junit.Assert;
import org.junit.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.security.crypto.codec.Base64;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import com.kite9.k9server.domain.User;

public class RestUserAndSecurityIT extends AbstractRestIT {

	private final class SilentErrorHandler implements ResponseErrorHandler {
		
		@Override
		public boolean hasError(ClientHttpResponse response) throws IOException {
			return false;
		}

		@Override
		public void handleError(ClientHttpResponse response) throws IOException {
		}
	}
	
	private ResponseEntity<User> retrieveUserViaBasicAuth(RestTemplate restTemplate, String password, String email) {
		String url = urlBase + "/api/users/retrieve?password="+password+"&email="+email;
		HttpEntity<String> entity = createBasicAuthHeaders(email, password);
		ResponseEntity<User> uOut = restTemplate.exchange(url, HttpMethod.GET, entity, User.class);
		return uOut;
	}
	
	private ResponseEntity<User> createUser(RestTemplate restTemplate, String username, String password, String email) {
		String url = urlBase + "/api/public/users/create?username="+username+"&password="+password+"&email="+email;
		ResponseEntity<User> uOut = restTemplate.getForEntity(url, User.class);
		return uOut;
	}
	
	private ResponseEntity<String> emailValidateRequest(RestTemplate restTemplate, String apiKey) {
		String url = urlBase + "/api/users/email-validation-request";
		HttpEntity<String> entity = createKite9AuthHeaders(apiKey);
		ResponseEntity<String> sOut = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
		return sOut;
	}
	
	private HttpEntity<String> createBasicAuthHeaders(String username, String password) {
		HttpHeaders headers = new HttpHeaders();
		String auth = username + ":" + password;
		byte[] encodedAuth = Base64.encode(auth.getBytes(Charset.forName("US-ASCII")));
		String authHeader = "Basic " + new String( encodedAuth );
		headers.set( HttpHeaders.AUTHORIZATION, authHeader );
		HttpEntity<String> entity = new HttpEntity<String>("parameters", headers);
		return entity;
	}

	private HttpEntity<String> createKite9AuthHeaders(String apiKey) {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
		headers.add(HttpHeaders.AUTHORIZATION, "KITE9 "+apiKey);
		HttpEntity<String> entity = new HttpEntity<String>("parameters", headers);
		return entity;
	}

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
		ResponseEntity<User> uOut = createUser(restTemplate, "MightyNew", "1234", email);
		User u = uOut.getBody();
		String apiKey = u.getApi();
		
		// make sure that the api can be called to email them
		ResponseEntity<String> resp = emailValidateRequest(restTemplate, apiKey);
		Assert.assertEquals("\"Please check your email for a message from Kite9 Support.\"", resp.getBody());
		
		// ensure that a wrong api key will fail the call
		resp = emailValidateRequest(restTemplate, "blardyblar");
		Assert.assertEquals(HttpStatus.UNAUTHORIZED, resp.getStatusCode());
	}

}
