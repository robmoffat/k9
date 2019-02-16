package com.kite9.k9server;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Base64;
import java.util.Map;

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
import org.springframework.hateoas.UriTemplate;
import org.springframework.hateoas.core.DefaultRelProvider;
import org.springframework.hateoas.hal.DefaultCurieProvider;
import org.springframework.hateoas.hal.Jackson2HalModule;
import org.springframework.hateoas.mvc.TypeReferences;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mail.MailSender;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.StreamUtils;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.kite9.k9server.resource.UserResource;
import com.kite9.k9server.web.WebConfig.LoggingFilter;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment=WebEnvironment.DEFINED_PORT)
public abstract class AbstractRestIT {
	
	@MockBean
	protected MailSender mailSender;
	
	protected String urlBase = "http://localhost:8080";
	public static TypeReferences.ResourcesType<UserResource> USER_RESOURCES_TYPE = new TypeReferences.ResourcesType<UserResource>() {};

	public AbstractRestIT() {
		super();
	}
	
	@Before
	public void logLevel() {
		Kite9Log.setLogging(false);
		LoggingSystem.get(this.getClass().getClassLoader()).setLogLevel(LoggingFilter.class.getName(), LogLevel.DEBUG);
	}
	
	public static StringHttpMessageConverter getStringConverter() {
		return new StringHttpMessageConverter();
	}
	
	public static FormHttpMessageConverter getFormConverter() {
		return new FormHttpMessageConverter();
	}

	public static MappingJackson2HttpMessageConverter getHALMessageConverter(){
	    ObjectMapper objectMapper = new ObjectMapper();
	    objectMapper.registerModule(new Jackson2HalModule());

	    //TODO: need to figure out this curie provider stuff...more in production mode
	    DefaultCurieProvider curieProvider = new DefaultCurieProvider("a", new UriTemplate("http://localhost:8080/{rel}"));
	    DefaultRelProvider relProvider = new DefaultRelProvider();

	    objectMapper.setHandlerInstantiator(new Jackson2HalModule.HalHandlerInstantiator(relProvider, curieProvider, null));

	    MappingJackson2HttpMessageConverter halConverter = new MappingJackson2HttpMessageConverter();
	    halConverter.setObjectMapper(objectMapper);
	    halConverter.setSupportedMediaTypes(Arrays.asList(MediaTypes.HAL_JSON, MediaType.APPLICATION_JSON));

	    return halConverter;
	}
	
	public static AbstractHttpMessageConverter<byte[]> getByteConverter() {
		
		return new AbstractHttpMessageConverter<byte[]>(MediaType.ALL) {

			@Override
			protected boolean supports(Class<?> clazz) {
				return (clazz.equals(byte[].class));
			}

			@Override
			protected byte[] readInternal(Class<? extends byte[]> clazz, HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
				return StreamUtils.copyToByteArray(inputMessage.getBody());
			}

			@Override
			protected void writeInternal(byte[] t, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
				StreamUtils.copy(t, outputMessage.getBody());
			}
		};
		
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
		
		RestTemplate template = new RestTemplateBuilder(new LoggingCustomizer(l)).messageConverters(getByteConverter(), getHALMessageConverter(), getStringConverter(), getFormConverter()).build();
		
		return template;
	}
	
	protected <X> X retrieveResource(RestTemplate restTemplate, String username, String password, String url, Class<X> outClass) throws URISyntaxException {
		RequestEntity<Void> in = new RequestEntity<Void>(createBasicAuthHeaders(password, username), HttpMethod.GET, new URI(url));
		ResponseEntity<X> out = restTemplate.exchange(in, TypeReferences.ResourceType.forType(outClass));
		return out.getBody();
	}
	
	protected <X> X retrieveResource(RestTemplate restTemplate, String jwt, String url, Class<X> outClass) throws URISyntaxException {
		RequestEntity<Void> in = new RequestEntity<Void>(createJWTTokenHeaders(jwt, null), HttpMethod.GET, new URI(url));
		ResponseEntity<X> out = restTemplate.exchange(in, TypeReferences.ResourceType.forType(outClass));
		return out.getBody();
	}
	
	protected <X, Y> ResponseEntity<X> exchangeUsingCookie(RestTemplate rt, String url, String cookie, Y in, HttpMethod method, Class<X> out) {
		HttpHeaders headers = new HttpHeaders();
		headers.add(HttpHeaders.COOKIE, cookie);
		HttpEntity<Y> requestEntity = new HttpEntity<Y>(in, headers);
		ResponseEntity<X> pOut = rt.exchange(url, method, requestEntity, out);
		return pOut;
	}

	protected HttpHeaders createBasicAuthHeaders(String password, String username) {
		HttpHeaders headers = new HttpHeaders();
		String auth = username + ":" + password;
		byte[] encodedAuth = java.util.Base64.getEncoder().encode(auth.getBytes(Charset.forName("US-ASCII")));
		String authHeader = "Basic " + new String( encodedAuth );
		headers.set( HttpHeaders.AUTHORIZATION, authHeader );
		return headers;
	}

	protected HttpHeaders createJWTTokenHeaders(String jwt, MediaType in, MediaType... accept) {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Arrays.asList(accept));
		if (in != null) {
			headers.setContentType(in);
		}
		headers.add(HttpHeaders.AUTHORIZATION, "Bearer "+jwt);
		return headers;
	}
	
	protected void delete(RestTemplate restTemplate, String url, String username, String password) throws URISyntaxException {
		HttpHeaders h = createBasicAuthHeaders(password, username);
		RequestEntity<Void> re = new RequestEntity<Void>(h, HttpMethod.DELETE, new URI(url));
		ResponseEntity<Void> out = restTemplate.exchange(re, Void.class);
		Assert.assertEquals(HttpStatus.NO_CONTENT, out.getStatusCode());
	}
	
	protected void delete(RestTemplate restTemplate, String url, String token) throws URISyntaxException {
		HttpHeaders h = createJWTTokenHeaders(token, null);
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