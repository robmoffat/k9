package com.kite9.k9server.adl.holder;

import org.apache.batik.transcoder.Transcoder;
import org.kite9.framework.xml.ADLDocument;
import org.w3c.dom.Node;

/**
 * ADL is our description of an XML format containing SVG data with mixed-in 
 * Kite9-namespace elements.  It is readable using the {@link org.kite9.framework.dom.ADLExtensibleDOMImplementation}.
 * 
 * @author robmoffat
 *
 */
public interface ADL {
	
	String getUri();
	
	String getAsXMLString();
	
	ADLDocument getAsDocument();

	Transcoder getTranscoder();
	
	String getAsXMLString(Node n);
	
}