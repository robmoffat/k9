package com.kite9.k9server.adl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;

import org.kite9.diagram.adl.Diagram;
import org.kite9.diagram.visualization.display.java2d.style.Stylesheet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.stereotype.Component;

import com.kite9.k9server.adl.arranger.DiagramArranger;
import com.kite9.k9server.adl.format.Format;
import com.kite9.k9server.adl.format.FormatSupplier;
import com.kite9.k9server.adl.format.MediaTypes;
import com.kite9.k9server.adl.holder.ADL;
import com.kite9.k9server.adl.holder.ADLImpl; 

@Component
public class ADLMessageConverter extends AbstractHttpMessageConverter<ADL>{

	public static final Charset DEFAULT = Charset.forName("UTF-8");
	
	@Autowired
	private DiagramArranger arranger;
	
	@Autowired
	private FormatSupplier formatSupplier;
	
	/**
	 * This is the list of media types we can support writing.
	 */
	public ADLMessageConverter() {
		super(MediaTypes.ADL_XML, MediaTypes.RENDERED_ADL_XML, MediaType.IMAGE_PNG, MediaTypes.SVG, MediaTypes.PDF, MediaType.TEXT_HTML);
	}

	@Override
	protected boolean supports(Class<?> clazz) {
		return ADL.class.isAssignableFrom(clazz);
	}
	
	/**
	 * List of things we can read in is much more limited than things we can write back out - just the XML formats, basically.
	 */
	@Override
	protected boolean canRead(MediaType mediaType) {
		return MediaTypes.ADL_XML.includes(mediaType) || MediaTypes.RENDERED_ADL_XML.includes(mediaType);
	}

	@Override
	protected ADL readInternal(Class<? extends ADL> clazz, HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
		MediaType mt = inputMessage.getHeaders().getContentType();
		Charset charset = mt.getCharSet();
		ByteArrayOutputStream baos = new ByteArrayOutputStream(10000);
		StreamHelp.streamCopy(inputMessage.getBody(), baos, true);
		String s = baos.toString(charset.name());
		return new ADLImpl(s, mt);
	}

	@Override
	protected void writeInternal(ADL t, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
		MediaType contentType = outputMessage.getHeaders().getContentType();
		Charset charset = contentType.getCharSet() == null ? Charset.forName("UTF-8") : contentType.getCharSet();
		String stylesheet = StylesheetProvider.DEFAULT;
		
		if (MediaTypes.ADL_XML.isCompatibleWith(contentType)) {
			outputMessage.getBody().write(t.getAsXMLString().getBytes(charset));
			return;
		}
			
		try {
			Diagram d = t.getAsDiagram();
			if (!t.isArranged()) {
				// unrendered, so render.
				d = arranger.arrangeDiagram(d, stylesheet);
			}

			Format f = formatSupplier.getFormatFor(contentType);
			Stylesheet ss = StylesheetProvider.getStylesheet(stylesheet);
			
			f.handleWrite(d, outputMessage.getBody(), ss, true, null, null);
		} catch (Exception e) {
			throw new HttpMessageNotReadableException("Caused by: "+e.getMessage(), e);
		}
		
	}
	
}
