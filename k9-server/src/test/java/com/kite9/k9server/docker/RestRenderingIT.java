package com.kite9.k9server.docker;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.imageio.ImageIO;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.junit.Assert;
import org.junit.Test;
import org.kite9.diagram.adl.Diagram;
import org.kite9.diagram.adl.Glyph;
import org.kite9.diagram.adl.Key;
import org.kite9.diagram.adl.Symbol;
import org.kite9.diagram.adl.Symbol.SymbolShape;
import org.kite9.diagram.adl.TextLine;
import org.kite9.diagram.position.DiagramRenderingInformation;
import org.kite9.diagram.position.RectangleRenderingInformation;
import org.kite9.framework.common.HelpMethods;
import org.kite9.framework.common.RepositoryHelp;
import org.kite9.framework.common.TestingHelp;
import org.kite9.framework.serialization.XMLFragments;
import org.kite9.framework.serialization.XMLHelper;
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

	protected byte[] withBytesInFormat(MediaType output) throws URISyntaxException {
		String xml = createDiagramXML();
		HttpHeaders headers = createKite9AuthHeaders(u.getApi(), MediaTypes.ADL_XML, output);
		RequestEntity<String> data = new RequestEntity<String>(xml, headers, HttpMethod.POST, new URI(urlBase+"/api/renderer"));
		byte[] back = getRestTemplate().exchange(data);
		return back;
	}
	
	protected byte[] withBytesFromFile(MediaType output) throws IOException, URISyntaxException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		StreamHelp.streamCopy(this.getClass().getResourceAsStream("/test-card.xml"), baos, true);
		HttpHeaders headers = createKite9AuthHeaders(u.getApi(), MediaTypes.ADL_XML, output);
		RequestEntity<String> data = new RequestEntity<String>(new String(baos.toByteArray()), headers, HttpMethod.POST, new URI(urlBase+"/api/renderer"));
		byte[] back = getRestTemplate().exchange(data);
		return back;
	}

	@Test
	public void testPNGRender() throws URISyntaxException, IOException {
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
	public void testPDFRender() throws URISyntaxException, IOException {
		byte[] back = withBytesInFormat(MediaTypes.PDF);
		
		ByteArrayInputStream bais = new ByteArrayInputStream(back);		
		PdfReader reader = new PdfReader(bais);
		Assert.assertEquals(1, reader.getNumberOfPages());
		Rectangle rect = reader.getPageSize(1);

		Assert.assertEquals(EXPECTED_WIDTH, (int) rect.getWidth());
		Assert.assertEquals(EXPECTED_HEIGHT, (int) rect.getHeight());
	}
	
	@Test
	public void testHTMLRender() throws URISyntaxException, IOException {
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
	public void testADLAndSVGRender() throws URISyntaxException {
		byte[] back = withBytesInFormat(MediaTypes.ADL_SVG);
		// ensure diagram has been rendered
		Diagram d = (Diagram) new XMLHelper().fromXML(new ByteArrayInputStream(back));
		DiagramRenderingInformation dri = d.getRenderingInformation();
		Assert.assertEquals(EXPECTED_WIDTH, (int)  dri.getSize().getWidth());
		Assert.assertEquals(EXPECTED_HEIGHT, (int)  dri.getSize().getHeight());	
		
		Glyph g1 = (Glyph) d.getContents().get(0);
		RectangleRenderingInformation rri = (RectangleRenderingInformation) g1.getRenderingInformation();
		XMLFragments frags = (XMLFragments) rri.getDisplayData();
		Assert.assertEquals(2, frags.getParts().size());
		
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
	
	@Test
	public void testXMLWithoutRender() throws URISyntaxException {
		byte[] back = withBytesInFormat(MediaTypes.ADL_XML);
		// ensure diagram has been not been rendered
		Diagram d = (Diagram) new XMLHelper().fromXML(new ByteArrayInputStream(back));
		DiagramRenderingInformation dri = d.getRenderingInformation();
		Assert.assertNull(dri.getHash());
	}

	public static Diagram createDiagram() {
		Diagram d = new Diagram(HelpMethods.createList(new Glyph("stereo", "Some Label", 
			HelpMethods.createList(
				new TextLine("Some Text Here To Make It A Bit Wider", 
						HelpMethods.createList(
								new Symbol("sdfs", 'W', SymbolShape.HEXAGON)))),				
			HelpMethods.createList(
				new Symbol("sdfsf", 's', SymbolShape.CIRCLE),
				new Symbol("sdfsf", 'w', SymbolShape.DIAMOND)))),
			
			new Key("bold", "body", 
				HelpMethods.createList(
					new Symbol("sdfs", 'W', SymbolShape.HEXAGON))));
		
		return d;
	}
	
	public static String createDiagramXML() {
		return new XMLHelper().toXML(createDiagram());
	}
}
