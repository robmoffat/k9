package com.kite9.k9server.adl.holder;

import java.io.StringReader;

import org.apache.batik.dom.util.DocumentFactory;
import org.apache.batik.transcoder.Transcoder;
import org.apache.batik.util.SVG12Constants;
import org.kite9.diagram.batik.format.Kite9SVGTranscoder;
import org.kite9.diagram.dom.ADLExtensibleDOMImplementation;
import org.kite9.diagram.dom.elements.ADLDocument;
import org.kite9.framework.common.Kite9ProcessingException;
import org.w3c.dom.Node;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSSerializer;


/**
 * Holds XML (either rendered (SVG) or unrendered (ADL) which will be output
 * from other Controllers in the system.  This can then be rendered into a given
 * content-type.  Also handles conversion to DOM Document format and back again if necessary.
 * 
 * @author robmoffat
 *
 */
public class ADLImpl implements ADL {
	
	enum Mode { STRING, DOM };
	
	private Mode m;
	private String xml;
	private ADLDocument doc;
	private String uri;
	private Kite9SVGTranscoder transcoder = new Kite9SVGTranscoder();

	public ADLImpl(String content, String uri) {
		this.xml = content;
		this.uri = uri;
		this.m = Mode.STRING;
	}

	public ADLImpl(ADLDocument doc) {
		this.doc = doc;
		this.m = Mode.DOM;
	}

	@Override
	public String getAsXMLString() {
		if (m == Mode.DOM) {
			xml = toXMLString(doc);
			m = Mode.STRING;
			doc = null;
		}
		
		return xml;
	}
	
	@Override
	public ADLDocument getAsDocument() {
		if (m == Mode.STRING) {
			doc = loadXMLDocument(xml, uri);
			m = Mode.DOM;
			xml = null;
		}
		return doc;
	}


	public static String toXMLString(Node n) {
		try {
			ADLDocument owner = (ADLDocument) (n instanceof ADLDocument ? n : n.getOwnerDocument());
			DOMImplementationLS ls = owner.getImplementation();
			LSSerializer ser = ls.createLSSerializer();
			return ser.writeToString(n);
		} catch (Exception e) {
			throw new Kite9ProcessingException("Couldn't serialize XML:", e);
		}
	}

	public ADLDocument loadXMLDocument(String content, String uri2) {
		DocumentFactory f = transcoder.getDocFactory();
		StringReader sr = new StringReader(content);
		try {
			ADLDocument document = (ADLDocument) f.createDocument(ADLExtensibleDOMImplementation.SVG_NAMESPACE_URI, SVG12Constants.SVG_SVG_TAG, uri2, sr);
			return document;
		} catch (Exception e) {
			throw new Kite9ProcessingException("Couldn't load XML into DOM: ", e);
		}
	}

	@Override
	public String getUri() {
		return uri;
	}

	@Override
	public Transcoder getTranscoder() {
		return transcoder;
	}

	@Override
	public String getAsXMLString(Node n) {
		return toXMLString(n);
	}
	
	
	
}
