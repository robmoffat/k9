package com.kite9.k9server.docker;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.imageio.ImageIO;

import org.junit.Assert;
import org.junit.Test;
import org.kite9.diagram.adl.Diagram;
import org.kite9.diagram.adl.Glyph;
import org.kite9.diagram.adl.Key;
import org.kite9.diagram.adl.Symbol;
import org.kite9.diagram.adl.Symbol.SymbolShape;
import org.kite9.diagram.adl.TextLine;
import org.kite9.diagram.position.DiagramRenderingInformation;
import org.kite9.framework.common.HelpMethods;
import org.kite9.framework.common.TestingHelp;
import org.kite9.framework.serialization.XMLHelper;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;

import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfReader;
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

	@Test
	public void testPNGRender() throws URISyntaxException, IOException {
		byte[] back = withBytesInFormat(MediaType.IMAGE_PNG);
		BufferedImage bi = ImageIO.read(new ByteArrayInputStream(back));
		Assert.assertEquals(EXPECTED_WIDTH, bi.getWidth());
		Assert.assertEquals(EXPECTED_HEIGHT, bi.getHeight());
	}
	
	@Test
	public void testPDFRender() throws URISyntaxException, IOException {
		byte[] back = withBytesInFormat(MediaTypes.PDF);
		File f = TestingHelp.prepareFileName(RestRenderingIT.class, "testPDFRenderer", "some.pdf");
//		StreamHelp.streamCopy(new ByteArrayInputStream(back), new FileOutputStream(f), true);
		
		ByteArrayInputStream bais = new ByteArrayInputStream(back);		
		PdfReader reader = new PdfReader(bais);
		Assert.assertEquals(1, reader.getNumberOfPages());
		Rectangle rect = reader.getPageSize(1);

		Assert.assertEquals(EXPECTED_WIDTH, (int) rect.getWidth());
		Assert.assertEquals(EXPECTED_HEIGHT, (int) rect.getHeight());
	}
	
	@Test
	public void testXMLRender() throws URISyntaxException {
		byte[] back = withBytesInFormat(MediaTypes.RENDERED_ADL_XML);
		// ensure diagram has been rendered
		Diagram d = (Diagram) new XMLHelper().fromXML(new ByteArrayInputStream(back));
		DiagramRenderingInformation dri = d.getRenderingInformation();
		Assert.assertEquals(EXPECTED_WIDTH, (int)  dri.getSize().getWidth());
		Assert.assertEquals(EXPECTED_HEIGHT, (int)  dri.getSize().getHeight());		
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
