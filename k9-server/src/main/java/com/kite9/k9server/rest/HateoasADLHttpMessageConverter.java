package com.kite9.k9server.rest;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.Charset;

import org.kite9.diagram.dom.XMLHelper;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.ResourceSupport;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractGenericHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.stereotype.Component;

import com.kite9.k9server.adl.format.FormatSupplier;
import com.kite9.k9server.adl.format.media.Format;
import com.kite9.k9server.adl.holder.ADL;
import com.kite9.k9server.security.Kite9HeaderMeta;

/**
 * Handles conversion of the Hateoas {@link ResourceSupport} objects to ADL, and therefore HTML, SVG etc..
 * All of the Kite9 domain objects extend {@link ResourceSupport}, and lists of them 
 * implement {@link org.springframework.hateoas.Resources}
 * 
 * @author robmoffat
 *
 */
@Component
public class HateoasADLHttpMessageConverter 
	extends AbstractGenericHttpMessageConverter<ResourceSupport> 
	implements InitializingBean {

	@Autowired
	HateoasDOMBuilder domBuilder;

	public static final Charset DEFAULT = Charset.forName("UTF-8");

	@Autowired
	protected FormatSupplier formatSupplier;

	@Override
	public void afterPropertiesSet() throws Exception {
		setSupportedMediaTypes(formatSupplier.getMediaTypes());
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
	protected void writeInternal(ResourceSupport t, Type type, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
		MediaType contentType = outputMessage.getHeaders().getContentType();	
		Format f = formatSupplier.getFormatFor(contentType);
		writeADL(t, outputMessage, f);
	}
	
	

	protected void writeADL(ResourceSupport t, HttpOutputMessage outputMessage, Format f) {
		ADL adl = null;
		try {
			adl = domBuilder.createDocument(t);
			Kite9HeaderMeta.addUserMeta(adl);
			f.handleWrite(adl, outputMessage.getBody(), true, null, null);
			 
		} catch (Exception e) {
			if (adl != null) {
				System.out.println(new XMLHelper().toXML(adl.getAsDocument()));
			}
			throw new HttpMessageNotWritableException("Caused by: "+e.getMessage(), e);
		}
	}


	@Override
	protected void addDefaultHeaders(HttpHeaders headers, ResourceSupport t, MediaType contentType) throws IOException {
		super.addDefaultHeaders(headers, t, contentType);
		Kite9HeaderMeta.addUserMeta(headers);
	}


	
}
