package com.kite9.k9server.adl.format.media;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;

import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.batik.anim.dom.SAXSVGDocumentFactory;
import org.apache.batik.anim.dom.SVG12DOMImplementation;
import org.apache.batik.anim.dom.SVG12OMDocument;
import org.apache.batik.transcoder.Transcoder;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.kite9.framework.common.Kite9ProcessingException;
import org.springframework.http.MediaType;
import org.springframework.util.StreamUtils;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.svg.SVGDocument;
import com.kite9.k9server.adl.holder.ADL;
import com.kite9.k9server.adl.holder.ADLImpl;

/**
 * Will eventually render the GUI, I guess?  Although, maybe we won't do it this way.
 * 
 * @author robmoffat
 *
 */
public class HTMLFormat implements Format {

	private static final String CONTENT_SEPARATOR = "{content}";
	public final String pageTemplateStart;
	public final String pageTemplateEnd;
	
	public HTMLFormat() {
		super();
		String pageTemplate;
		try {
			pageTemplate = StreamUtils.copyToString(this.getClass().getResourceAsStream("/page-template.html"), Charset.defaultCharset());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		int contentStart = pageTemplate.indexOf(CONTENT_SEPARATOR);
		pageTemplateStart = pageTemplate.substring(0, contentStart);
		pageTemplateEnd = pageTemplate.substring(contentStart+CONTENT_SEPARATOR.length());
	}

	@Override
	public MediaType[] getMediaTypes() {
		return new MediaType[] { MediaType.TEXT_HTML };
	}
	
	@Override
	public void handleWrite(ADL adl, OutputStream baos, boolean watermark, Integer width, Integer height) throws Exception {
		baos.write(pageTemplateStart.getBytes());
		baos.write(getSVGRepresentation(adl));
		baos.write(pageTemplateEnd.getBytes());
	}

	public String getExtension() {
		return ".html";
	}
	
	/**
	 * Returns just the content element, not the DOCTYPE, PI etc.
	 */
	public byte[] getSVGRepresentation(ADL data) throws Exception {
		try {
			Transcoder transcoder = data.getTranscoder();
			TranscoderInput in = new TranscoderInput(data.getAsDocument());
			in.setURI(data.getUri());
			TranscoderOutput to = new TranscoderOutput();
			transcoder.transcode(in, to);
			Element e = to.getDocument().getDocumentElement();
			String xmlString = ADLImpl.toXMLString(e, true);
			return xmlString.getBytes();
		} catch (TranscoderException e) {
			throw new Kite9ProcessingException(e);
		}
	}

}
