package com.kite9.k9server.adl;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.kite9.framework.common.RepositoryHelp;
import org.kite9.framework.common.TestingHelp;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfReader;
import com.kite9.k9server.AbstractAuthenticatedIT;
import com.kite9.k9server.AbstractUserBasedTest;
import com.kite9.k9server.adl.StreamHelp;
import com.kite9.k9server.adl.format.MediaTypes;
import com.kite9.k9server.adl.holder.ADL;
import com.kite9.k9server.adl.holder.ADLImpl;


/**
 * Tests that basic Kite9 ADL rendering functionality is working.
 * 
 * @author robmoffat
 *
 */
public class RestRenderingIT extends AbstractUserBasedTest {
	
	private static final int EXPECTED_HEIGHT = 400;
	public static final int EXPECTED_WIDTH = 400;

	protected byte[] withBytesInFormat(MediaType output) throws Exception {
		String xml = createDiagramXML();
		HttpHeaders headers = createKite9AuthHeaders(u.api, MediaTypes.ADL_SVG, output);
		HttpEntity<byte[]> postBody = new HttpEntity<byte[]>(xml.getBytes(), headers);
		
		ResponseEntity<byte[]> back = getRestTemplate().exchange(new URI(urlBase+"/api/renderer"), HttpMethod.POST, postBody, byte[].class);
		return back.getBody();
	}
//	
//	protected String withBytesFromFile(MediaType output) throws IOException, URISyntaxException {
//		ByteArrayOutputStream baos = new ByteArrayOutputStream();
//		StreamHelp.streamCopy(this.getClass().getResourceAsStream("/test-card.xml"), baos, true);
//		HttpHeaders headers = createKite9AuthHeaders(u.api, MediaTypes.ADL_SVG, output);
//		RequestEntity<String> data = new RequestEntity<String>(new String(baos.toByteArray()), headers, HttpMethod.POST, new URI(urlBase+"/api/renderer"));
//		ResponseEntity<String> back= getRestTemplate().exchange(data, String.class);
//		return back.getBody();
//	}

//	@Test
//	@Ignore("No working PNG renderer right now")
//	public void testPNGRender() throws Exception {
//		byte[] back = withBytesInFormat(MediaType.IMAGE_PNG);
//		BufferedImage bi = ImageIO.read(new ByteArrayInputStream(back));
//		Assert.assertEquals(EXPECTED_WIDTH, bi.getWidth());
//		Assert.assertEquals(EXPECTED_HEIGHT, bi.getHeight());
//	}
	
//	@Test
//	@Ignore("No working PNG renderer right now")
//	public void testPNGRenderFromFile() throws Exception {
//		byte[] back = withBytesInFormat(MediaType.IMAGE_PNG);
//		BufferedImage bi = ImageIO.read(new ByteArrayInputStream(back));
//		File f = TestingHelp.prepareFileName(this.getClass(),"testPNGRenderFromFile", "diagram.png");
////		ImageIO.write(bi, "PNG", f);
//		persistInAFile(back, "testPNGRenderFromFile", "diagram.png");
//		
//		BufferedImage bi2 = ImageIO.read(new FileInputStream(f));
//		Assert.assertEquals(956, bi2.getWidth());
//	}

	public void persistInAFile(byte[] back, String test, String filename) throws IOException, FileNotFoundException {
		File f = TestingHelp.prepareFileName(this.getClass(),test, filename);
		RepositoryHelp.streamCopy(new ByteArrayInputStream(back), new FileOutputStream(f), true);
	}
	
//	@Test
//	@Ignore("No working PDF renderer right now")
//	public void testPDFRender() throws Exception {
//		byte[] back = withBytesInFormat(MediaTypes.PDF);
//		
//		ByteArrayInputStream bais = new ByteArrayInputStream(back);		
//		PdfReader reader = new PdfReader(bais);
//		Assert.assertEquals(1, reader.getNumberOfPages());
//		Rectangle rect = reader.getPageSize(1);
//
//		Assert.assertEquals(EXPECTED_WIDTH, (int) rect.getWidth());
//		Assert.assertEquals(EXPECTED_HEIGHT, (int) rect.getHeight());
//	}
	
	@Test
	public void testHTMLRender() throws Exception {
		byte[] back = withBytesInFormat(MediaType.TEXT_HTML);
		persistInAFile(back, "testHTMLRender", "diagram.html");

		String s = new String(back);
		
		Assert.assertTrue(s.contains(
			" <renderingInformation xsi:type=\"diagram-ri\" rendered=\"true\">\n"+
			"  <displayData xsi:type=\"org.kite9.framework.serialization.XMLFragments\">\n"+
			"   <defs id=\"defs1\">\n"));
		
		Assert.assertTrue(s.endsWith("</html>"));
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
		URL u = this.getClass().getResource("/stylesheets/designer.css");
		return "<svg:defs><svg:style type=\"text/css\"> @import url(\""+u+"\");</svg:style></svg:defs>";
	}
	
	public String getJavascriptReference() {
		URL u = this.getClass().getResource("/some.js");
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
		StreamHelp.streamCopy(new InputStreamReader(this.getClass().getResourceAsStream("/test1.xml")), sw, true);
		String theDiagram = sw.toString();
		return addSVGFurniture(theDiagram);
	}
}
