package com.kite9.k9server.adl;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.URI;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Arrays;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StreamUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.kite9.k9server.AbstractRestIT;
import com.kite9.k9server.XMLCompare;
import com.kite9.k9server.adl.format.media.MediaTypes;


/**
 * Tests that basic Kite9 ADL rendering functionality is working.
 * 
 * This is an open endpoint with no security.
 * 
 * @author robmoffat
 *
 */
public class PostRenderingIT extends AbstractRestIT {
	
	protected byte[] withBytesInFormat(MediaType output) throws Exception {
		String xml = createDiagramXML();
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Arrays.asList(output));
		headers.setContentType( MediaTypes.ADL_SVG);
		HttpEntity<byte[]> postBody = new HttpEntity<byte[]>(xml.getBytes(), headers);
		
		ResponseEntity<byte[]> back = getRestTemplate().exchange(new URI(getUrlBase()+"/api/renderer"), HttpMethod.POST, postBody, byte[].class);
		return back.getBody();
	}
	
	@Test
	public void testHTMLRender() throws Exception {
		byte[] back = withBytesInFormat(MediaType.TEXT_HTML);
		persistInAFile(back, "testHTMLRender", "diagram.html");
		XMLCompare.compareXML(StreamUtils.copyToString(this.getClass().getResourceAsStream("/rendering/post/test1.html"), Charset.forName("UTF-8")), new String(back));
	}

	
	@Test
	public void testADLAndSVGRender() throws Exception {
		byte[] back = withBytesInFormat(MediaTypes.ADL_SVG);
		// ensure diagram hasn't been rendered
		String out = new String(back);
		Assert.assertTrue(out.contains("id=\"auto:0-one\" rank=\"0\""));
		Assert.assertTrue(out.contains("<glyph"));
	}
	
	@Test
	public void testSVGRender() throws Exception {
		byte[] back = withBytesInFormat(MediaTypes.SVG);
		persistInAFile(back, "testSVGRender", "diagram.svg");

		// parse it to make sure it's good svg
		Document d = parseBytesToXML(back);
		
		Element e = d.getDocumentElement();
		Assert.assertEquals("svg", e.getTagName());
		Assert.assertEquals(4, e.getChildNodes().getLength());
		XMLCompare.compareXML(StreamUtils.copyToString(this.getClass().getResourceAsStream("/rendering/post/test1.svg"), Charset.forName("UTF-8")), new String(back));
	}

	public Document parseBytesToXML(byte[] back) throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setValidating(false);
		dbf.setFeature( "http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
		dbf.setExpandEntityReferences(false);
		DocumentBuilder db= dbf.newDocumentBuilder();
		Document d = db.parse(
			new InputSource(new ByteArrayInputStream(back)));
		return d;
	}
	
	public String getDesignerStylesheetReference() {
		URL u = this.getClass().getResource("/rendering/post/designer-server.css");
		return "<svg:defs><svg:style type=\"text/css\"> @import url(\""+u+"\");</svg:style></svg:defs>";
	}
	
	public String getJavascriptReference() {
		URL u = this.getClass().getResource("/rendering/post/some.js");
		return "<svg:script type=\"text/ecmascript\" xlink:href=\""+u.toString()+"\"/>";
	}

	protected String addSVGFurniture(String xml) {
		String prefix = "<svg:svg xmlns:xlink='http://www.w3.org/1999/xlink' xmlns:svg='http://www.w3.org/2000/svg'>";
		String style = getDesignerStylesheetReference();
		String javascript = getJavascriptReference();
		String suffix = "</svg:svg>";
		xml = xml.replaceFirst("<\\?.*\\?>","");
		String full = prefix + style + javascript+ xml + suffix;
		return full;
	}
	
	public String createDiagramXML() throws IOException {
		StringWriter sw = new StringWriter();
		StreamHelp.streamCopy(new InputStreamReader(this.getClass().getResourceAsStream("/rendering/post/test1.xml")), sw, true);
		String theDiagram = sw.toString();
		return addSVGFurniture(theDiagram);
	}
}
