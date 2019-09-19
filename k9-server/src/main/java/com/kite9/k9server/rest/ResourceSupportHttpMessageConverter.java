package com.kite9.k9server.rest;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.Ordered;
import org.springframework.core.io.ResourceLoader;
import org.springframework.hateoas.ResourceSupport;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.GenericHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.stereotype.Component;

import com.kite9.k9server.adl.format.AbstractFormatBasedConverter;
import com.kite9.k9server.adl.format.media.Format;
import com.kite9.k9server.adl.holder.ADL;

/**
 * Handles conversion of the Hateoas {@link ResourceSupport} objects to ADL, and therefore HTML, SVG etc..
 * All of the Kite9 domain objects extend {@link ResourceSupport}, and lists of them 
 * implement {@link org.springframework.hateoas.Resources}
 * 
 * @author robmoffat
 *
 */
@Component
public class ResourceSupportHttpMessageConverter 
	extends AbstractFormatBasedConverter<ResourceSupport> 
	implements Ordered, GenericHttpMessageConverter<ResourceSupport> {

	@Value("${kite9.rest.template:classpath:/templates/api/document.xml}")
	private String templateResource;
	
	private String template;
	
	@Autowired
	ResourceLoader resourceLoader;
	
	@Autowired
	ResourceSupportDOMBuilder domBuilder;
	
	
	@Override
	public void afterPropertiesSet() throws Exception {
		super.afterPropertiesSet();
		template = IOUtils.toString(resourceLoader.getResource(templateResource).getInputStream(), "UTF-8");
	}

	@Override
	protected boolean supports(Class<?> clazz) {
		return ResourceSupport.class.isAssignableFrom(clazz);
	}

	@Override
	protected boolean canRead(MediaType mediaType) {
		return false;	// this is for display formats only.
	}

	@Override
	public int getOrder() {
		return Ordered.LOWEST_PRECEDENCE-5;
	}

	@Override
	public boolean canRead(Type type, Class<?> contextClass, MediaType mediaType) {
		return false;
	}

	@Override
	public ResourceSupport read(Type type, Class<?> contextClass, HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
		throw new UnsupportedOperationException("Can't read with this converter");
	}
	
	@Override
	protected ResourceSupport readInternal(Class<? extends ResourceSupport> clazz, HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
		throw new UnsupportedOperationException("Can't read with this converter");
	}


	@Override
	public boolean canWrite(Type type, Class<?> clazz, MediaType mediaType) {
		return super.canWrite(clazz, mediaType);
	}

	@Override
	public void write(ResourceSupport t, Type type, MediaType contentType, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
		Format f = formatSupplier.getFormatFor(contentType);
		writeADL(t, outputMessage, f);
	}


	@Override
	protected void writeInternal(ResourceSupport t, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
		MediaType contentType = outputMessage.getHeaders().getContentType();	
		Format f = formatSupplier.getFormatFor(contentType);
		writeADL(t, outputMessage, f);
	}

	protected void writeADL(ResourceSupport t, HttpOutputMessage outputMessage, Format f) {
		try {
			ADL adl = convertToADL(t, outputMessage.getHeaders().getLocation(), outputMessage.getHeaders());
			f.handleWrite(adl, outputMessage.getBody(), true, null, null);
			
		} catch (Exception e) {
			throw new HttpMessageNotWritableException("Caused by: "+e.getMessage(), e);
		}
	}
	
	protected ADL convertToADL(ResourceSupport t, URI u, HttpHeaders headers) {
		return domBuilder.createDocument(t, template, u, headers);
	}

}
