package com.kite9.k9server.adl.format;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;

import org.kite9.diagram.adl.Diagram;
import org.kite9.diagram.visualization.display.java2d.style.Stylesheet;
import org.kite9.framework.serialization.XMLHelper;
import org.springframework.http.MediaType;

import com.google.common.io.Resources;

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
			pageTemplate = Resources.toString(this.getClass().getResource("/page-template.html"), Charset.defaultCharset());
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
	public void handleWrite(Diagram arrangedDiagram, OutputStream baos, Stylesheet ss, boolean watermark, Integer width, Integer height) throws IOException {
		XMLHelper helper = new XMLHelper();
		String xml = helper.toXML(arrangedDiagram);
		
		OutputStreamWriter wos1 = new OutputStreamWriter(baos);
		wos1.write(pageTemplateStart);
		writeXMLScriptTag(xml, wos1);
		wos1.write(pageTemplateEnd);
		wos1.flush();
	}

	private void writeXMLScriptTag(String xml, OutputStreamWriter wos1) throws IOException {
		wos1.write("<script type=\"text/xml\" id=\"data\">\n");
		wos1.write(xml);
		wos1.write("\n</script>\n");
	}

	@Override
	public String getExtension() {
		return ".html";
	}

}
