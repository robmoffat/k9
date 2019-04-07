package com.kite9.k9server.adl.format.media;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.Map;

import org.apache.batik.transcoder.TranscoderOutput;
import org.springframework.http.MediaType;
import org.springframework.util.StreamUtils;

import com.kite9.k9server.adl.holder.ADL;

/**
 * Will eventually render the GUI, I guess?  Although, maybe we won't do it this way.
 * 
 * @author robmoffat
 *
 */
public class HTMLFormat implements Format {
	
	private static final String HEADERS_SEPARATOR = "{headers}";
	private static final String CONTENT_SEPARATOR = "{content}";
	public final String pageTemplateStart;
	public final String pageTemplateMiddle;
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
		int headerStart = pageTemplate.indexOf(HEADERS_SEPARATOR);
		pageTemplateStart = pageTemplate.substring(0, headerStart);
		pageTemplateMiddle = pageTemplate.substring(headerStart+HEADERS_SEPARATOR.length(), contentStart);
		pageTemplateEnd = pageTemplate.substring(contentStart+CONTENT_SEPARATOR.length());
	}

	@Override
	public MediaType[] getMediaTypes() {
		return new MediaType[] { MediaType.TEXT_HTML };
	}
	
	@Override
	public void handleWrite(ADL adl, OutputStream baos, boolean watermark, Integer width, Integer height) throws Exception {
		baos.write(pageTemplateStart.getBytes());
		for (Map.Entry<String, String> e : adl.getMetaData().entrySet()) {
			baos.write("    <meta property=\"kite9:".getBytes());
			baos.write(e.getKey().getBytes());
			baos.write("\" content=\"".getBytes());
			baos.write(e.getValue().getBytes());
			baos.write("\" />".getBytes());
		}
		baos.write(pageTemplateMiddle.getBytes());
		TranscoderOutput to = new TranscoderOutput(baos);
		adl.getTranscoder().writeSVGToOutput(adl.getSVGRepresentation(), to);
		baos.write(pageTemplateEnd.getBytes());
	}

	public String getExtension() {
		return ".html";
	}

}
