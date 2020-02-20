package com.kite9.k9server;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import org.apache.commons.logging.impl.SimpleLog;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.kite9.framework.common.TestingHelp;
import org.kite9.framework.logging.Kite9Log;
import org.springframework.boot.logging.LogLevel;
import org.springframework.boot.logging.LoggingSystem;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.UriTemplate;
import org.springframework.hateoas.mediatype.MessageResolver;
import org.springframework.hateoas.mediatype.hal.DefaultCurieProvider;
import org.springframework.hateoas.mediatype.hal.Jackson2HalModule;
import org.springframework.hateoas.server.core.DefaultLinkRelationProvider;
import org.springframework.hateoas.server.core.TypeReferences;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mail.MailSender;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.StreamUtils;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.kite9.k9server.command.Command;
import com.kite9.k9server.resource.UserResource;
import com.kite9.k9server.web.WebConfig.LoggingFilter;

/**
 * Configuration of basic test, and rest template for accessing endpoints.
 * 
 * @author robmoffat
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment=WebEnvironment.DEFINED_PORT)
@TestPropertySource(properties = {
		"server.port=0"})
public abstract class AbstractRestIT {
	
	@MockBean
	protected MailSender mailSender;
	
	@LocalServerPort
	protected int port;
	
	protected String getUrlBase()  {
		return "http://localhost:"+port;
	}
	
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
	    DefaultLinkRelationProvider relProvider = new DefaultLinkRelationProvider();

	    objectMapper.setHandlerInstantiator(new Jackson2HalModule.HalHandlerInstantiator(relProvider, curieProvider, MessageResolver.DEFAULTS_ONLY));

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
	
	protected HttpHeaders createTokenHeaders(String token, MediaType in, MediaType... accept) {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Arrays.asList(accept));
		if (in != null) {
			headers.setContentType(in);
		}
		headers.add(HttpHeaders.AUTHORIZATION, "token "+token);
		return headers;
	}
	
	protected HttpHeaders createNoAuthHeaders(MediaType in, MediaType... accept) {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Arrays.asList(accept));
		if (in != null) {
			headers.setContentType(in);
		}
		return headers;
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
	
	public void persistInAFile(byte[] back, String test, String filename) throws IOException, FileNotFoundException {
		File f = TestingHelp.prepareFileName(this.getClass(),test, filename);
		FileOutputStream fos = new FileOutputStream(f);
		ByteArrayInputStream zis = new ByteArrayInputStream(back);
		StreamUtils.copy(zis, fos);
	}
	
	
	public static class CommandList extends ArrayList<Command> {

		public CommandList(Collection<? extends Command> c) {
			super(c);
		}
		
		public CommandList(Command c) {
			super();
			add(c);
		}
	}

}