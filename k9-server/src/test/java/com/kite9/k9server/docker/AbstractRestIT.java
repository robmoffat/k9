package com.kite9.k9server.docker;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.security.crypto.codec.Base64;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kite9.k9server.domain.User;

public class AbstractRestIT extends AbstractDockerIT {

	protected ObjectMapper mapper = new ObjectMapper();
	protected String urlBase = "http://" + getDockerHostName()+ ":8080";

	public AbstractRestIT() {
		super();
	}
	
	protected RestTemplate getRestTemplate() {
		return new RestTemplate(new SimpleClientHttpRequestFactory());
	}

	
	protected ResponseEntity<User> retrieveUserViaBasicAuth(RestTemplate restTemplate, String password, String email) {
		String url = urlBase + "/api/users/retrieve?password="+password+"&email="+email;
		HttpEntity<String> entity = createBasicAuthHeaders(email, password);
		ResponseEntity<User> uOut = restTemplate.exchange(url, HttpMethod.GET, entity, User.class);
		return uOut;
	}
	
	protected <X> ResponseEntity<X> postAsTestUser(RestTemplate restTemplate, X object, Class<X> theClass, String url, User u) {
		HttpEntity<X> entity = createKite9AuthHeaders(u.getApi(), object);
		ResponseEntity<X> pOut = getRestTemplate().exchange(urlBase + url, HttpMethod.POST, entity, theClass);
		return pOut;
	}
	
	protected ResponseEntity<User> createUser(RestTemplate restTemplate, String username, String password, String email) {
		String url = urlBase + "/api/public/users";
		Map<String, String> user = new HashMap<>();
		user.put("username", username);
		user.put("password", password);
		user.put("email", email);
		ResponseEntity<User> uOut = restTemplate.postForEntity(url, user, User.class);
		return uOut;
	}
	
	protected ResponseEntity<String> emailValidateRequest(RestTemplate restTemplate, String apiKey) {
		String url = urlBase + "/api/users/email-validation-request";
		HttpEntity<String> entity = createKite9AuthHeaders(apiKey, "parameters");
		ResponseEntity<String> sOut = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
		return sOut;
	}
	
	protected HttpEntity<String> createBasicAuthHeaders(String username, String password) {
		HttpHeaders headers = new HttpHeaders();
		String auth = username + ":" + password;
		byte[] encodedAuth = Base64.encode(auth.getBytes(Charset.forName("US-ASCII")));
		String authHeader = "Basic " + new String( encodedAuth );
		headers.set( HttpHeaders.AUTHORIZATION, authHeader );
		HttpEntity<String> entity = new HttpEntity<String>("parameters", headers);
		return entity;
	}

	protected <X> HttpEntity<X> createKite9AuthHeaders(String apiKey, X body) {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
		headers.add(HttpHeaders.AUTHORIZATION, "KITE9 "+apiKey);
		HttpEntity<X> entity = new HttpEntity<X>(body, headers);
		return entity;
	}
	
	protected final class SilentErrorHandler implements ResponseErrorHandler {
		
		@Override
		public boolean hasError(ClientHttpResponse response) throws IOException {
			return false;
		}

		@Override
		public void handleError(ClientHttpResponse response) throws IOException {
		}
	}
}