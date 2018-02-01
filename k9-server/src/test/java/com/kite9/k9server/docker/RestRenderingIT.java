package com.kite9.k9server.docker;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
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
import org.junit.runner.RunWith;
import org.kite9.framework.common.RepositoryHelp;
import org.kite9.framework.common.TestingHelp;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfReader;
import com.kite9.k9server.adl.StreamHelp;
import com.kite9.k9server.adl.format.MediaTypes;


/**
 * Tests that basic Kite9 ADL rendering functionality is working.
 * 
 * @author robmoffat
 *
 */
public class RestRenderingIT extends AbstractAuthenticatedIT {
	
	private static final int EXPECTED_HEIGHT = 204;
	public static final int EXPECTED_WIDTH = 264;

	protected byte[] withBytesInFormat(MediaType output) throws Exception {
		String xml = createDiagramXML();
		HttpHeaders headers = createKite9AuthHeaders(u.getApi(), MediaTypes.ADL_SVG, output);
		RequestEntity<String> data = new RequestEntity<String>(xml, headers, HttpMethod.POST, new URI(urlBase+"/api/renderer"));
		byte[] back = getRestTemplate().exchange(data);
		return back;
	}
	
	protected byte[] withBytesFromFile(MediaType output) throws IOException, URISyntaxException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		StreamHelp.streamCopy(this.getClass().getResourceAsStream("/test-card.xml"), baos, true);
		HttpHeaders headers = createKite9AuthHeaders(u.getApi(), MediaTypes.ADL_SVG, output);
		RequestEntity<String> data = new RequestEntity<String>(new String(baos.toByteArray()), headers, HttpMethod.POST, new URI(urlBase+"/api/renderer"));
		byte[] back = getRestTemplate().exchange(data);
		return back;
	}

	@Test
	public void testPNGRender() throws Exception {
		byte[] back = withBytesInFormat(MediaType.IMAGE_PNG);
		BufferedImage bi = ImageIO.read(new ByteArrayInputStream(back));
		Assert.assertEquals(EXPECTED_WIDTH, bi.getWidth());
		Assert.assertEquals(EXPECTED_HEIGHT, bi.getHeight());
	}
	
	@Test
	public void testPNGRenderFromFile() throws URISyntaxException, IOException {
		byte[] back = withBytesFromFile(MediaType.IMAGE_PNG);
		persistInAFile(back, "testPNGRenderFromFile", "diagram.png");
		BufferedImage bi = ImageIO.read(new ByteArrayInputStream(back));
		Assert.assertEquals(956, bi.getWidth());
	}

	public void persistInAFile(byte[] back, String test, String filename) throws IOException, FileNotFoundException {
		File f = TestingHelp.prepareFileName(this.getClass(),test, filename);
		RepositoryHelp.streamCopy(new ByteArrayInputStream(back), new FileOutputStream(f), true);
	}
	
	@Test
	@Ignore("No working PDF renderer right now")
	public void testPDFRender() throws Exception {
		byte[] back = withBytesInFormat(MediaTypes.PDF);
		
		ByteArrayInputStream bais = new ByteArrayInputStream(back);		
		PdfReader reader = new PdfReader(bais);
		Assert.assertEquals(1, reader.getNumberOfPages());
		Rectangle rect = reader.getPageSize(1);

		Assert.assertEquals(EXPECTED_WIDTH, (int) rect.getWidth());
		Assert.assertEquals(EXPECTED_HEIGHT, (int) rect.getHeight());
	}
	
	@Ignore("Sprint 17: Broken HTML format right now")
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
		Assert.assertTrue(out.contains("<glyph id=\"auto:0-one\" rank=\"0\">"));
	}
	
	@Test
	public void testSVGRender() throws Exception {
		byte[] back = withBytesInFormat(MediaTypes.SVG);
		persistInAFile(back, "testSVGRender", "diagram.svg");

		// parse it to make sure it's good svg
		Document d = parseBytesToXML(back);
		
		Element e = d.getDocumentElement();
		Assert.assertEquals("svg", e.getTagName());
		Assert.assertEquals(3, e.getChildNodes().getLength());
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

	protected String addSVGFurniture(String xml) {
		String prefix = "<svg:svg xmlns:xlink='http://www.w3.org/1999/xlink' xmlns:svg='http://www.w3.org/2000/svg'>";
		String style = getDesignerStylesheetReference();
		String suffix = "</svg:svg>";
		xml = xml.replaceFirst("<\\?.*\\?>","");
		String full = prefix + style + xml + suffix;
		return full;
	}
	
	public String createDiagramXML() throws IOException {
		StringWriter sw = new StringWriter();
		StreamHelp.streamCopy(new InputStreamReader(this.getClass().getResourceAsStream("/test1.xml")), sw, true);
		String theDiagram = sw.toString();
		return addSVGFurniture(theDiagram);
	}
}
