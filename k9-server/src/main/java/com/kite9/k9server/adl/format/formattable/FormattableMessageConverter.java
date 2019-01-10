package com.kite9.k9server.adl.format.formattable;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.stereotype.Component;

import com.kite9.k9server.adl.StreamHelp;
import com.kite9.k9server.adl.format.AbstractFormatBasedConverter;
import com.kite9.k9server.adl.format.media.Format;
import com.kite9.k9server.adl.format.media.MediaTypes;
import com.kite9.k9server.adl.holder.ADL;
import com.kite9.k9server.adl.holder.ADLImpl;
import com.kite9.k9server.domain.revision.Revision;
import com.kite9.k9server.domain.revision.RevisionRepository; 

@Component
public class FormattableMessageConverter extends AbstractFormatBasedConverter<Formattable> {
	
	@Autowired
	private RevisionRepository revisionRepository;
	
	
	@Override
	protected boolean supports(Class<?> clazz) {
		return Formattable.class.isAssignableFrom(clazz);
	}
	
	@Override
	protected boolean canWrite(MediaType mediaType) {
		return super.canWrite(mediaType);
	}

	/**
	 * List of things we can read in is much more limited than things we can write back out - just the XML formats, basically.
	 */
	@Override
	protected boolean canRead(MediaType mediaType) {
		return false;
//		return MediaTypes.SVG.includes(mediaType) || MediaTypes.ADL_SVG.includes(mediaType);
	}

	@Override
	protected Formattable readInternal(Class<? extends Formattable> clazz, HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
//		MediaType mt = inputMessage.getHeaders().getContentType();
//		Charset charset = mt.getCharset();
//		ByteArrayOutputStream baos = new ByteArrayOutputStream(10000);
//		StreamHelp.streamCopy(inputMessage.getBody(), baos, true);
//		String s = baos.toString(charset.name());
//		ADL adl = new ADLImpl(s, "someurl");
//		return new AbstractFormattable() {
//
//			@Override
//			public ADL getInput() {
//				return adl;
//			}
//
//			@Override
//			public boolean requiresSave() {
//				return false;
//			}
//		};
		return null;
	}

	@Override
	protected void writeInternal(Formattable t, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
		MediaType contentType = outputMessage.getHeaders().getContentType();	
		try {
			Format f = formatSupplier.getFormatFor(contentType);
			f.handleWrite(t, outputMessage.getBody(), this, true, null, null);
		} catch (Exception e) {
			throw new HttpMessageNotReadableException("Caused by: "+e.getMessage(), e);
		}
	}
	
}
