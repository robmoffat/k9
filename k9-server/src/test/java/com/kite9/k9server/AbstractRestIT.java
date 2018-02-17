package com.kite9.k9server;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.commons.logging.impl.SimpleLog;
import org.junit.Assert;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.kite9.framework.logging.Kite9Log;
import org.springframework.boot.logging.LogLevel;
import org.springframework.boot.logging.LoggingSystem;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.hateoas.UriTemplate;
import org.springframework.hateoas.core.DefaultRelProvider;
import org.springframework.hateoas.hal.DefaultCurieProvider;
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
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mail.MailSender;
import org.springframework.security.crypto.codec.Base64;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.kite9.k9server.domain.User;
import com.kite9.k9server.web.WebConfig.LoggingFilter;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment=WebEnvironment.DEFINED_PORT)
public class AbstractRestIT {
	
	@MockBean
	MailSender mailSender;

	protected String urlBase = "http://localhost:8080";
	public static TypeReferences.ResourceType<User> USER_RESOURCE_TYPE = new TypeReferences.ResourceType<User>() {};
	public static TypeReferences.ResourcesType<User> USER_RESOURCES_TYPE = new TypeReferences.ResourcesType<User>() {};

	public AbstractRestIT() {
		super();
	}
	
	@Before
	public void logLevel() {
		Kite9Log.setLogging(false);
		LoggingSystem.get(this.getClass().getClassLoader()).setLogLevel(LoggingFilter.class.getName(), LogLevel.DEBUG);
	}
	
//	private MappingJackson2HttpMessageConverter getJacksonConverter(RestTemplate rt) {
//		for (HttpMessageConverter<?> c : rt.getMessageConverters()) {
//			if (c instanceof MappingJackson2HttpMessageConverter) {
//				return (MappingJackson2HttpMessageConverter) c;
//			}
//		}
//		
//		throw new RuntimeException("Couldn't find Jackson converter!");
//	}
	
	public static MappingJackson2HttpMessageConverter getHALMessageConverter(){
	    ObjectMapper objectMapper = new ObjectMapper();
	    objectMapper.registerModule(new Jackson2HalModule());

	    //TODO: need to figure out this curie provider stuff...more in production mode
	    DefaultCurieProvider curieProvider = new DefaultCurieProvider("a", new UriTemplate("http://localhost:8080/myapp/rels/{rel}"));
	    DefaultRelProvider relProvider = new DefaultRelProvider();

	    objectMapper.setHandlerInstantiator(new Jackson2HalModule.HalHandlerInstantiator(relProvider, curieProvider, null));

	    MappingJackson2HttpMessageConverter halConverter = new MappingJackson2HttpMessageConverter();
	    halConverter.setObjectMapper(objectMapper);
	    halConverter.setSupportedMediaTypes(Arrays.asList(MediaTypes.HAL_JSON));

	    return halConverter;
	}
	
	/**
	 * Provides a REST Template that supports HAL and logging.
	 */
	protected RestTemplate getRestTemplate() {
//		MediaHandlingRestTemplate template = new MediaHandlingRestTemplate(new SimpleClientHttpRequestFactory());
		MappingJackson2HttpMessageConverter converter = getHALMessageConverter();
		ObjectMapper mapper = converter.getObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		mapper.registerModule(new Jackson2HalModule());
		mapper.enable(SerializationFeature.INDENT_OUTPUT);
		
		SimpleLog l = new SimpleLog("TEST");
		l.setLevel(SimpleLog.LOG_LEVEL_DEBUG);
		
		RestTemplate template = new RestTemplateBuilder(new LoggingCustomizer(l)).messageConverters(Collections.singleton(converter)).build();
		
		return template;
	}
	
	protected <X> Resource<X> retrieveResource(RestTemplate restTemplate, User u, String url, Class<X> outClass) throws URISyntaxException {
		RequestEntity<Void> in = new RequestEntity<Void>(createKite9AuthHeaders(u.getApi()), HttpMethod.GET, new URI(url));
		ResponseEntity<Resource<X>> out = restTemplate.exchange(in, TypeReferences.ResourceType.forType(outClass));
		return out.getBody();
	}
	
	protected Resources<User> retrieveUserViaBasicAuth(RestTemplate restTemplate, String password, String email) throws URISyntaxException {
		String url = urlBase + "/api/users";
		HttpHeaders headers = new HttpHeaders();
		String auth = email + ":" + password;
		byte[] encodedAuth = Base64.encode(auth.getBytes(Charset.forName("US-ASCII")));
		String authHeader = "Basic " + new String( encodedAuth );
		headers.set( HttpHeaders.AUTHORIZATION, authHeader );
		RequestEntity<Void> entity = new RequestEntity<Void>(headers, HttpMethod.GET, new URI(url));
		ResponseEntity<Resources<User>> uOut = restTemplate.exchange(entity, USER_RESOURCES_TYPE);
		return uOut.getBody(); 
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
	
	
	protected ResponseEntity<User> createUser(RestTemplate restTemplate, String username, String password, String email) throws URISyntaxException {
		String url = urlBase + "/api/users";
		User u = new User(username, password, email);
		RequestEntity<User> re = new RequestEntity<>(u, HttpMethod.POST, new URI(url));
		ResponseEntity<User> uOut = restTemplate.exchange(re, User.class);
		return uOut;
	}


	protected HttpHeaders createKite9AuthHeaders(String apiKey) {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Arrays.asList(MediaType.ALL));
		headers.setContentType(MediaTypes.HAL_JSON);
		headers.add(HttpHeaders.AUTHORIZATION, "KITE9 "+apiKey);
		return headers;
	}
	
	protected HttpHeaders createKite9AuthHeaders(String apiKey, MediaType in, MediaType... accept) {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Arrays.asList(accept));
		headers.setContentType(in);
		headers.add(HttpHeaders.AUTHORIZATION, "KITE9 "+apiKey);
		return headers;
	}
	
	protected void delete(RestTemplate restTemplate, String url, User u) throws URISyntaxException {
		HttpHeaders h = createKite9AuthHeaders(u.getApi());
		RequestEntity<Void> re = new RequestEntity<Void>(h, HttpMethod.DELETE, new URI(url));
		ResponseEntity<Void> out = restTemplate.exchange(re, Void.class);
		Assert.assertEquals(HttpStatus.NO_CONTENT, out.getStatusCode());
	}
	
	protected <X> void deleteAndCheckDeleted(RestTemplate restTemplate, String url, User u, Class<X> c) throws URISyntaxException {
		delete(restTemplate, url, u);
		try {
			retrieveResource(restTemplate, u, url, c);
			Assert.fail();
		} catch (HttpClientErrorException e) {
			// should throw this.
		} 
		
		
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
	
//	public class MediaHandlingRestTemplate extends RestTemplate {
//
//		public MediaHandlingRestTemplate(ClientHttpRequestFactory requestFactory) {
//			super(requestFactory);
//			
//		}
//		
//		public byte[] exchange(RequestEntity<?> requestEntity) throws RestClientException {
//
//			org.springframework.util.Assert.notNull(requestEntity, "'requestEntity' must not be null");
//
//			RequestCallback requestCallback = httpEntityCallback(requestEntity, null);
//			return execute(requestEntity.getUrl(), requestEntity.getMethod(), requestCallback, new ResponseExtractor<byte[]>() {
//
//				@Override
//				public byte[] extractData(ClientHttpResponse response) throws IOException {
//					int contentLength = (int) response.getHeaders().getContentLength();
//					ByteArrayOutputStream baos = new ByteArrayOutputStream(Math.max(contentLength, 6000));
//					StreamHelp.streamCopy(response.getBody(), baos, true);
//					return baos.toByteArray();
//				}
//			});
//		}
//
//	}

}