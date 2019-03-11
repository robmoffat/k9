package com.kite9.k9server.adl.holder;

import org.kite9.diagram.batik.format.Kite9SVGTranscoder;
import org.kite9.diagram.dom.elements.ADLDocument;
import org.w3c.dom.Node;

/**
 * ADL is our description of an XML format containing SVG data with mixed-in 
 * Kite9-namespace elements.  It is readable using the {@link org.kite9.diagram.dom.ADLExtensibleDOMImplementation}.
 * 
 * @author robmoffat
 *
 */
public interface ADL {
	
	String getUri();
	
	String getAsXMLString();
	
	ADLDocument loadXMLDocument(String content, String uri);
	
	ADLDocument getAsDocument();

	Kite9SVGTranscoder getTranscoder();
	
	String getAsXMLString(Node n);
	
	/**
	 * Returns the hash of a given node, or for the whole xml document if none given.
	 */
	String hash(String id);
	
}