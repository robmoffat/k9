package com.kite9.k9server.adl.holder;

/**
 * ADL is our description of an XML format containing SVG data with mixed-in 
 * Kite9-namespace elements.  It is readable using the {@link org.kite9.framework.dom.ADLExtensibleDOMImplementation}.
 * 
 * @author robmoffat
 *
 */
public interface ADL {
	
	String getAsXMLString();

}