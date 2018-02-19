package com.kite9.k9server;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;

import org.apache.commons.logging.impl.SimpleLog;
import org.junit.Assert;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.kite9.framework.logging.Kite9Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.logging.LogLevel;
import org.springframework.boot.logging.LoggingSystem;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.Resource;
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
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mail.MailSender;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.kite9.k9server.adl.ADLMessageConverter;
import com.kite9.k9server.resource.UserResource;
import com.kite9.k9server.web.WebConfig.LoggingFilter;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment=WebEnvironment.DEFINED_PORT)
public class AbstractRestIT {
	
	@MockBean
	protected MailSender mailSender;
	
	@Autowired
	private ADLMessageConverter adlMessageConverter;

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
		
		RestTemplate template = new RestTemplateBuilder(new LoggingCustomizer(l)).messageConverters(adlMessageConverter, getHALMessageConverter()).build();
		
		return template;
	}
	
	protected <X> Resource<X> retrieveResource(RestTemplate restTemplate, UserResource u, String url, Class<X> outClass) throws URISyntaxException {
		RequestEntity<Void> in = new RequestEntity<Void>(createKite9AuthHeaders(u.api), HttpMethod.GET, new URI(url));
		ResponseEntity<Resource<X>> out = restTemplate.exchange(in, TypeReferences.ResourceType.forType(outClass));
		return out.getBody();
	}
	
	protected <X, Y> ResponseEntity<X> exchangeUsingCookie(RestTemplate rt, String url, String cookie, Y in, HttpMethod method, Class<X> out) {
		HttpHeaders headers = new HttpHeaders();
		headers.add(HttpHeaders.COOKIE, cookie);
		HttpEntity<Y> requestEntity = new HttpEntity<Y>(in, headers);
		ResponseEntity<X> pOut = rt.exchange(url, method, requestEntity, out);
		return pOut;
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
	
	protected void delete(RestTemplate restTemplate, String url, UserResource u) throws URISyntaxException {
		HttpHeaders h = createKite9AuthHeaders(u.api);
		RequestEntity<Void> re = new RequestEntity<Void>(h, HttpMethod.DELETE, new URI(url));
		ResponseEntity<Void> out = restTemplate.exchange(re, Void.class);
		Assert.assertEquals(HttpStatus.NO_CONTENT, out.getStatusCode());
	}
	
	protected <X> void deleteAndCheckDeleted(RestTemplate restTemplate, String url, UserResource u, Class<X> c) throws URISyntaxException {
		delete(restTemplate, url, u);
		try {
			retrieveResource(restTemplate, u, url, c);
			Assert.fail();
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