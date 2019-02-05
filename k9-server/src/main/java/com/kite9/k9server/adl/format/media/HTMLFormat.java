package com.kite9.k9server.adl.format.media;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;

import org.springframework.http.MediaType;
import org.springframework.util.StreamUtils;

import com.kite9.k9server.adl.format.Converter;
import com.kite9.k9server.adl.holder.ADL;

/**
 * Will eventually render the GUI, I guess?  Although, maybe we won't do it this way.
 * 
 * @author robmoffat
 *
 */
public class HTMLFormat extends SVGFormat {

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
	public void handleWrite(ADL adl, OutputStream baos, Converter c, boolean watermark, Integer width, Integer height) throws Exception {
		baos.write(pageTemplateStart.getBytes());
		super.handleWrite(adl, baos, c, watermark, width, height);
		baos.write(pageTemplateEnd.getBytes());
	}

	public String getExtension() {
		return ".html";
	}

}
