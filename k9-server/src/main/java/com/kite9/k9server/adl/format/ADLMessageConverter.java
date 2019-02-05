package com.kite9.k9server.adl.format;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.Charset;

import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.stereotype.Component;

import com.kite9.k9server.adl.StreamHelp;
import com.kite9.k9server.adl.format.media.Format;
import com.kite9.k9server.adl.format.media.MediaTypes;
import com.kite9.k9server.adl.holder.ADL;
import com.kite9.k9server.adl.holder.ADLImpl; 

@Component
public class ADLMessageConverter extends AbstractFormatBasedConverter<ADL> {
	
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
	protected ADL readInternal(Class<? extends ADL> clazz, HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
		MediaType mt = inputMessage.getHeaders().getContentType();
		Charset charset = mt.getCharset();
		ByteArrayOutputStream baos = new ByteArrayOutputStream(10000);
		StreamHelp.streamCopy(inputMessage.getBody(), baos, true);
		String s = baos.toString(charset.name());
		URI location = inputMessage.getHeaders().getLocation();
		String uri = location != null ? location.toString() : "anyuri";
		ADL adl = new ADLImpl(s, uri);
		return adl;
	}

	@Override
	protected void writeInternal(ADL t, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
		MediaType contentType = outputMessage.getHeaders().getContentType();	
		try {
			Format f = formatSupplier.getFormatFor(contentType);
			f.handleWrite(t, outputMessage.getBody(), this, true, null, null);
		} catch (Exception e) {
			throw new HttpMessageNotWritableException("Caused by: "+e.getMessage(), e);
		}
	}
	
}
