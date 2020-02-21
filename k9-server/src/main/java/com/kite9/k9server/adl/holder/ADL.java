package com.kite9.k9server.adl.holder;

import java.net.URI;
import java.util.Map;

import org.kite9.diagram.batik.format.Kite9SVGTranscoder;
import org.kite9.diagram.dom.elements.ADLDocument;
import org.kite9.framework.common.Kite9ProcessingException;
import org.springframework.http.HttpHeaders;
import org.w3c.dom.Document;

/**
 * ADL is our description of an XML format containing SVG data with mixed-in 
 * Kite9-namespace elements.  It is readable using the {@link org.kite9.diagram.dom.ADLExtensibleDOMImplementation}.
 * 
 * @author robmoffat
 *
 */
public interface ADL {
	
	URI getUri();
	
	void setUri(URI u);
	
	String getAsADLString();
	
	ADLDocument getAsADLDocument();

	Kite9SVGTranscoder getTranscoder();
	
	/**
	 * Returns properties that might be useful for display on the screen of the editor.
	 */
	void setMeta(String name, String value);
	
	/**
	 * Information about the editing, creation etc. of the document.
	 */
	Map<String, String> getMetaData();
	
	/**
	 * Performs the transformation to create the SVG representation.
	 * This will be cached once created.
	 */
	Document getAsSVGRepresentation() throws Kite9ProcessingException;

	/**
	 * Returns the HTTPHeaders that were responsible for loading this 
	 * ADL.  Useful for passing around credentials.
	 */
	HttpHeaders getRequestHeaders();
	
	/**
	 * For loading up a referenced document.
	 */
	ADLDocument loadRelatedDocument(URI uri);
	
	/**
	 * For parsing a referenced document.
	 */
	ADLDocument parseDocument(String content, URI uri);
	
}