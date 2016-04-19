package com.kite9.k9server.docker;

import java.io.ByteArrayInputStream;
import java.net.URI;
import java.net.URISyntaxException;

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
import org.kite9.framework.serialization.XMLHelper;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;

import com.kite9.k9server.adl.format.MediaTypes;


/**
 * Tests that basic Kite9 ADL rendering functionality is working.
 * 
 * @author robmoffat
 *
 */
public class RestRenderingIT extends AbstractAuthenticatedIT {
	
//	@Test
//	public void testPDFRender() throws URISyntaxException {
//		String xml = createDiagramXML();
//		HttpHeaders headers = createKite9AuthHeaders(u.getApi(), MediaTypes.ADL_XML, MediaType.IMAGE_PNG);
//		RequestEntity<String> data = new RequestEntity<String>(xml, headers, HttpMethod.POST, new URI(urlBase+"/api/renderer"));
//		ClientHttpResponse back = getRestTemplate().exchange(data);
//		
//	}
	
	@Test
	public void testXMLRender() throws URISyntaxException {
		String xml = createDiagramXML();
		HttpHeaders headers = createKite9AuthHeaders(u.getApi(), MediaTypes.ADL_XML, MediaTypes.RENDERED_ADL_XML);
		RequestEntity<String> data = new RequestEntity<String>(xml, headers, HttpMethod.POST, new URI(urlBase+"/api/renderer"));
		byte[] back = getRestTemplate().exchange(data);
		
		Diagram d = (Diagram) new XMLHelper().fromXML(new ByteArrayInputStream(back));
		DiagramRenderingInformation dri = d.getRenderingInformation();
		Assert.assertEquals(144, (int)  dri.getSize().getWidth());
		Assert.assertEquals(204, (int)  dri.getSize().getHeight());		
	}

	protected Diagram createDiagram() {
		Diagram d = new Diagram(HelpMethods.createList(new Glyph("stereo", "Some Label", 
			HelpMethods.createList(
				new TextLine("Some Text Here", 
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
	
	protected String createDiagramXML() {
		return new XMLHelper().toXML(createDiagram());
	}
}
