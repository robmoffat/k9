package com.kite9.k9server;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.springframework.hateoas.Resources;
import org.springframework.hateoas.mvc.TypeReferences;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.kite9.k9server.domain.project.Project;
import com.kite9.k9server.resource.UserResource;

/**
 * Makes sure there is a new user available to send REST requests with.
 * 
 * @author robmoffat
 *
 */
public abstract class AbstractAuthenticatedIT extends AbstractRestIT {

	protected RestTemplate restTemplate;
	
	@Before
	public void setupRestTemplate() {
		this.restTemplate = getRestTemplate();
	}

	protected UserResource createUser(RestTemplate restTemplate, String username, String password, String email) throws URISyntaxException {
		String url = getUrlBase() + "/api/users";
		UserResource u = new UserResource(username, password, email, Project.createRandomString());
		RequestEntity<UserResource> re = new RequestEntity<>(u, HttpMethod.POST, new URI(url));
		ResponseEntity<UserResource> uOut = restTemplate.exchange(re, UserResource.class);
		return uOut.getBody();
	}

	protected <X, Y> ResponseEntity<X> exchangeUsingCookie(RestTemplate rt, String url, String cookie, Y in, HttpMethod method, Class<X> out) {
		HttpHeaders headers = new HttpHeaders();
		headers.add(HttpHeaders.COOKIE, cookie);
		HttpEntity<Y> requestEntity = new HttpEntity<Y>(in, headers);
		ResponseEntity<X> pOut = rt.exchange(url, method, requestEntity, out);
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
		
		ResponseEntity<String> s = restTemplate.postForEntity(getUrlBase()+"/login", in, String.class);
		return s;
	}

	protected Resources<UserResource> retrieveUserViaBasicAuth(RestTemplate restTemplate, String password, String email) throws URISyntaxException {
		String url = getUrlBase() + "/api/users";
		HttpHeaders headers = createBasicAuthHeaders(password, email);
		RequestEntity<Void> entity = new RequestEntity<Void>(headers, HttpMethod.GET, new URI(url));
		ResponseEntity<Resources<UserResource>> uOut = restTemplate.exchange(entity, USER_RESOURCES_TYPE);
		return uOut.getBody(); 
	}
	
	protected Resources<UserResource> retrieveUserViaJwt(RestTemplate restTemplate, String jwt) throws URISyntaxException {
		String url = getUrlBase() + "/api/users";
		HttpHeaders headers = createJWTTokenHeaders(jwt, null);
		RequestEntity<Void> entity = new RequestEntity<Void>(headers, HttpMethod.GET, new URI(url));
		ResponseEntity<Resources<UserResource>> uOut = restTemplate.exchange(entity, USER_RESOURCES_TYPE);
		return uOut.getBody(); 
	}
	
	protected String getJwtToken(RestTemplate restTemplate, String username, String password) {
		String href=getUrlBase()+"/oauth/token";
		MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
		map.add("grant_type", "client_credentials");
		HttpHeaders headers = createBasicAuthHeaders(password, username);
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		HttpEntity<MultiValueMap<String, String>> ent = new HttpEntity<>(map, headers);
		ResponseEntity<OAuth2AccessToken> resp = restTemplate.exchange(href, HttpMethod.POST, ent, OAuth2AccessToken.class);
		String jwtToken = resp.getBody().getValue();
		return jwtToken;
	}

	protected void delete(RestTemplate restTemplate, String url, String token) throws URISyntaxException {
		HttpHeaders h = createJWTTokenHeaders(token, null);
		RequestEntity<Void> re = new RequestEntity<Void>(h, HttpMethod.DELETE, new URI(url));
		ResponseEntity<Void> out = restTemplate.exchange(re, Void.class);
		Assert.assertEquals(HttpStatus.NO_CONTENT, out.getStatusCode());
	}

	protected void delete(RestTemplate restTemplate, String url, String username, String password) throws URISyntaxException {
		HttpHeaders h = createBasicAuthHeaders(password, username);
		RequestEntity<Void> re = new RequestEntity<Void>(h, HttpMethod.DELETE, new URI(url));
		ResponseEntity<Void> out = restTemplate.exchange(re, Void.class);
		Assert.assertEquals(HttpStatus.NO_CONTENT, out.getStatusCode());
	}

	protected <X> void deleteAndCheckDeleted(RestTemplate restTemplate, String url, String jwtToken, Class<X> c) throws URISyntaxException {
		delete(restTemplate, url, jwtToken);
		try {
			retrieveResource(restTemplate, jwtToken, url, c);
			Assert.fail();
		} catch (AssertionError ae) {
			throw ae;
		} catch (Throwable e) {
			// should throw this.
			e.printStackTrace();
		} 
		
		
	}

	protected <X> void deleteAndCheckDeleted(RestTemplate restTemplate, String url, String username, String password, Class<X> c) throws URISyntaxException {
		delete(restTemplate, url, username, password);
		try {
			retrieveResource(restTemplate, username, password, url, c);
			Assert.fail();
		} catch (AssertionError ae) {
			throw ae;
		} catch (Throwable e) {
			// should throw this.
			e.printStackTrace();
		} 
		
		
	}

	protected <X> X retrieveResource(RestTemplate restTemplate, String jwt, String url, Class<X> outClass) throws URISyntaxException {
		RequestEntity<Void> in = new RequestEntity<Void>(createJWTTokenHeaders(jwt, null), HttpMethod.GET, new URI(url));
		ResponseEntity<X> out = restTemplate.exchange(in, TypeReferences.ResourceType.forType(outClass));
		return out.getBody();
	}

	protected <X> X retrieveResource(RestTemplate restTemplate, String username, String password, String url, Class<X> outClass) throws URISyntaxException {
		RequestEntity<Void> in = new RequestEntity<Void>(createBasicAuthHeaders(password, username), HttpMethod.GET, new URI(url));
		ResponseEntity<X> out = restTemplate.exchange(in, TypeReferences.ResourceType.forType(outClass));
		return out.getBody();
	}
		
}
