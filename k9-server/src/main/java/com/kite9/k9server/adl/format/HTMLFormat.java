package com.kite9.k9server.adl.format;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;

import org.kite9.diagram.batik.format.ResourceReferencer;
import org.springframework.http.MediaType;

import com.google.common.io.Resources;
import com.kite9.k9server.adl.holder.ADL;

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
	public void handleWrite(ADL xml, OutputStream baos, boolean watermark, Integer width, Integer height, ResourceReferencer rr) throws Exception {
//		ImageRenderingPipeline<String> p = new ImageRenderingPipeline<String>(new GriddedCompleteDisplayer(new ADLBasicCompleteDisplayer(ss, watermark, false),ss),
//				new ADLAndSVGRenderer(width, height));
//		
//		String xml = p.render(arrangedDiagram);
//		OutputStreamWriter wos1 = new OutputStreamWriter(baos);
//		wos1.write(pageTemplateStart);
//		writeXMLScriptTag(xml, wos1);
//		wos1.write(pageTemplateEnd);
//		wos1.flush();
	}

	private void writeXMLScriptTag(String xml, OutputStreamWriter wos1) throws IOException {
		wos1.write("<script type=\"text/xml\" id=\"data\">");
		wos1.write(xml);
		wos1.write("</script>\n");
	}

	public String getExtension() {
		return ".html";
	}

}
