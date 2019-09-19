package com.kite9.k9server.adl.format;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.List;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.stereotype.Component;

import com.kite9.k9server.adl.StreamHelp;
import com.kite9.k9server.adl.format.media.Format;
import com.kite9.k9server.adl.format.media.MediaTypes;
import com.kite9.k9server.adl.holder.ADL;
import com.kite9.k9server.adl.holder.ADLImpl;
import com.kite9.k9server.security.Kite9HeaderMeta; 

@Component
public class ADLMessageConverter extends AbstractHttpMessageConverter<ADL> implements InitializingBean {
	

	public static final Charset DEFAULT = Charset.forName("UTF-8");

	@Autowired
	protected FormatSupplier formatSupplier;

	@Override
	public void afterPropertiesSet() throws Exception {
		setSupportedMediaTypes(formatSupplier.getMediaTypes());
	}
	
	@Override
	protected boolean supports(Class<?> clazz) {
		return ADL.class.isAssignableFrom(clazz);
	}
	
	@Override
	protected boolean canWrite(MediaType mediaType) {
		return super.canWrite(mediaType);
	}

	@Override
	protected boolean canRead(MediaType mediaType) {
		return MediaTypes.SVG.includes(mediaType) || MediaTypes.ADL_SVG.includes(mediaType);
	}

	@Override
	public List<MediaType> getSupportedMediaTypes() {
		return super.getSupportedMediaTypes();
	}

	@Override
	protected ADL readInternal(Class<? extends ADL> clazz, HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
		MediaType mt = inputMessage.getHeaders().getContentType();
		Charset charset = mt.getCharset();
		ByteArrayOutputStream baos = new ByteArrayOutputStream(10000);
		StreamHelp.streamCopy(inputMessage.getBody(), baos, true);
		String s = baos.toString(charset.name());
		URI uri = inputMessage.getHeaders().getLocation();
		ADL adl = new ADLImpl(s, uri, inputMessage.getHeaders());
		return adl;
	}

	@Override
	protected void writeInternal(ADL t, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
		MediaType contentType = outputMessage.getHeaders().getContentType();	
		try {
			Format f = formatSupplier.getFormatFor(contentType);
			f.handleWrite(t, outputMessage.getBody(), true, null, null);
			
		} catch (Exception e) {
			throw new HttpMessageNotWritableException("Caused by: "+e.getMessage(), e);
		}
	}
	
	@Override
	protected void addDefaultHeaders(HttpHeaders headers, ADL t, MediaType contentType) throws IOException {
		super.addDefaultHeaders(headers, t, contentType);
		Kite9HeaderMeta.addUserMeta(t);
		Kite9HeaderMeta.transcribeMetaToHeaders(t, headers);
	}


	
}
