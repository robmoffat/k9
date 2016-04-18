package com.kite9.k9server.docker;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.hateoas.hal.Jackson2HalModule;
import org.springframework.hateoas.mvc.TypeReferences;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.crypto.codec.Base64;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kite9.k9server.domain.User;

public class AbstractRestIT extends AbstractDockerIT {

	protected String urlBase = "http://" + getDockerHostName()+ ":8080";
	public static TypeReferences.ResourceType<User> USER_RESOURCE_TYPE = new TypeReferences.ResourceType<User>() {};
	public static TypeReferences.ResourcesType<User> USER_RESOURCES_TYPE = new TypeReferences.ResourcesType<User>() {};

	public AbstractRestIT() {
		super();
	}
	
	private MappingJackson2HttpMessageConverter getJacksonConverter(RestTemplate rt) {
		for (HttpMessageConverter<?> c : rt.getMessageConverters()) {
			if (c instanceof MappingJackson2HttpMessageConverter) {
				return (MappingJackson2HttpMessageConverter) c;
			}
		}
		
		throw new RuntimeException("Couldn't find Jackson converter!");
	}
	
	/**
	 * Provides a REST Template that supports HAL, and also doesn't throw exceptions as errors (instead, analyse the 
	 * HTTP Status returned.
	 * @return
	 */
	protected RestTemplate getRestTemplate() {
		RestTemplate template = new RestTemplate(new SimpleClientHttpRequestFactory());
		MappingJackson2HttpMessageConverter converter = getJacksonConverter(template);
		
		ObjectMapper mapper = converter.getObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		mapper.registerModule(new Jackson2HalModule());
		
		template.setErrorHandler(new SilentErrorHandler());
		return template;
	}
	
	protected <X> ResponseEntity<X> retrieveObjectViaApiAuth(RestTemplate restTemplate, User u, String url, Class<X> outClass) throws URISyntaxException {
		RequestEntity<Void> in = new RequestEntity<Void>(createKite9AuthHeaders(u.getApi()), HttpMethod.GET, new URI(url));
		ResponseEntity<X> out = restTemplate.exchange(in, outClass);
		return out;
	}
	
	protected ResponseEntity<Resources<User>> retrieveUserViaBasicAuth(RestTemplate restTemplate, String password, String email) throws URISyntaxException {
		String url = urlBase + "/api/users";
		HttpHeaders headers = new HttpHeaders();
		String auth = email + ":" + password;
		byte[] encodedAuth = Base64.encode(auth.getBytes(Charset.forName("US-ASCII")));
		String authHeader = "Basic " + new String( encodedAuth );
		headers.set( HttpHeaders.AUTHORIZATION, authHeader );
		RequestEntity<Void> entity = new RequestEntity<Void>(headers, HttpMethod.GET, new URI(url));
		ResponseEntity<Resources<User>> uOut = restTemplate.exchange(entity, USER_RESOURCES_TYPE);
		return uOut; 
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
	
	
	protected ResponseEntity<Resource<User>> createUser(RestTemplate restTemplate, String username, String password, String email) throws URISyntaxException {
		String url = urlBase + "/api/users";
		User u = new User(username, password, email);
		RequestEntity<User> re = new RequestEntity<User>(u, HttpMethod.POST, new URI(url));
		ResponseEntity<Resource<User>> uOut = restTemplate.exchange(re, USER_RESOURCE_TYPE);
		return uOut;
	}


	protected HttpHeaders createKite9AuthHeaders(String apiKey) {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Arrays.asList(MediaType.ALL));
		headers.setContentType(MediaTypes.HAL_JSON);
		headers.add(HttpHeaders.AUTHORIZATION, "KITE9 "+apiKey);
		return headers;
	}
	
	protected void delete(RestTemplate restTemplate, String url, User u) throws URISyntaxException {
		HttpHeaders h = createKite9AuthHeaders(u.getApi());
		RequestEntity<Void> re = new RequestEntity<Void>(h, HttpMethod.DELETE, new URI(url));
		ResponseEntity<Void> out = restTemplate.exchange(re, Void.class);
		Assert.assertEquals(HttpStatus.NO_CONTENT, out.getStatusCode());
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