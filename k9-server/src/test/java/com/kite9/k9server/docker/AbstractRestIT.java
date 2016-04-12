package com.kite9.k9server.docker;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.security.crypto.codec.Base64;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kite9.k9server.domain.User;

import org.junit.Assert;

public class AbstractRestIT extends AbstractDockerIT {

	protected ObjectMapper mapper = new ObjectMapper();
	protected String urlBase = "http://" + getDockerHostName()+ ":8080";

	public AbstractRestIT() {
		super();
	}
	
	protected RestTemplate getRestTemplate() {
		return new RestTemplate(new SimpleClientHttpRequestFactory());
	}
	
	protected <X> ResponseEntity<X> retrieveObjectViaApiAuth(RestTemplate restTemplate, User u, String url, Class<X> outClass) {
		HttpEntity<String> in = createKite9AuthHeaders(u.getApi(), "");
		ResponseEntity<X> out = restTemplate.exchange(url, HttpMethod.GET, in, outClass);
		return out;
	}

	protected void deleteViaApiAuth(RestTemplate restTemplate, String url, User u) {
		HttpEntity<String> in = createKite9AuthHeaders(u.getApi(), "");
		ResponseEntity<String> out = restTemplate.exchange(url, HttpMethod.DELETE, in, String.class);
		Assert.assertEquals(HttpStatus.NO_CONTENT, out.getStatusCode());
	}
	
	protected ResponseEntity<User> retrieveUserViaBasicAuth(RestTemplate restTemplate, String password, String email) {
		String url = urlBase + "/api/users";
		HttpEntity<String> entity = createBasicAuthHeaders(email, password);
		ResponseEntity<User> uOut = restTemplate.exchange(url, HttpMethod.GET, entity, User.class);
		return uOut;
	}
	
	protected <X> ResponseEntity<X> postAsTestUser(RestTemplate restTemplate, X object, Class<X> theClass, String url, User u) {
		HttpEntity<X> entity = createKite9AuthHeaders(u.getApi(), object);
		ResponseEntity<X> pOut = getRestTemplate().exchange(url, HttpMethod.POST, entity, theClass);
		return pOut;
	}
	
	protected ResponseEntity<String> formLogin(RestTemplate restTemplate, String user, String password) {
		List<HttpMessageConverter<?>> converters = restTemplate.getMessageConverters();
		converters.add(new FormHttpMessageConverter());
		MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
		map.add("username", user);
		map.add("password", password);
		map.add("submit", "Login");
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		HttpEntity<MultiValueMap<String, String>> in = new HttpEntity<MultiValueMap<String,String>>(map, headers);
		
		ResponseEntity<String> s = restTemplate.postForEntity(urlBase+"/login", in, String.class);
		return s;
	}
	
	protected <X, Y> ResponseEntity<X> exchangeUsingCookie(RestTemplate rt, String url, String cookie, Y in, HttpMethod method, Class<X> out) {
		HttpHeaders headers = new HttpHeaders();
		headers.add(HttpHeaders.COOKIE, cookie);
		HttpEntity<Y> requestEntity = new HttpEntity<Y>(in, headers);
		ResponseEntity<X> pOut = rt.exchange(url, method, requestEntity, out);
		return pOut;
	}
	
	
	protected ResponseEntity<User> createUser(RestTemplate restTemplate, String username, String password, String email) {
		String url = urlBase + "/api/public/users";
		User u = new User(username, password, email);
		ResponseEntity<User> uOut = restTemplate.postForEntity(url, u, User.class);
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
		headers.setAccept(Arrays.asList(MediaType.ALL));
		headers.setContentType(MediaType.APPLICATION_JSON);
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