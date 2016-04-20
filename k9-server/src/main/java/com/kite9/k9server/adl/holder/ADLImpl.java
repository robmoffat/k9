package com.kite9.k9server.adl.holder;

import org.kite9.diagram.adl.Diagram;
import org.kite9.framework.serialization.XMLHelper;
import org.springframework.http.MediaType;

/**
 * Holds DiagramML (either rendered or unrendered) which will be output from other Controllers in the system.
 * This can then be rendered into a given content-type.
 * 
 * @author robmoffat
 *
 */
public class ADLImpl implements ADL {
		
	private Diagram diagram;
	private String xml;
	private final MediaType mt;
	
	public ADLImpl(String content, MediaType mt) {
		this.xml = content;
		this.mt = mt;
	}
	public ADLImpl(Diagram content, MediaType mt) {
		this.diagram = content;
		this.mt = mt;
	}

	@Override
	public Diagram getAsDiagram() {
		if (diagram == null) {
			diagram = (Diagram) new XMLHelper().fromXML(xml);
		}
		
		return diagram;
	}

	@Override
	public MediaType getMediaType() {
		return mt;
	}
	@Override
	public String getAsXMLString() {
		if (xml == null) {
			xml = new XMLHelper().toXML(diagram);
		}
		
		return xml;
	}
	
}
