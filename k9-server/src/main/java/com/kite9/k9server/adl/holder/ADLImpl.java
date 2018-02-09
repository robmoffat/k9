package com.kite9.k9server.adl.holder;

import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.batik.dom.util.DocumentFactory;
import org.apache.batik.transcoder.Transcoder;
import org.apache.batik.util.SVG12Constants;
import org.kite9.diagram.batik.format.Kite9SVGTranscoder;
import org.kite9.framework.common.Kite9ProcessingException;
import org.kite9.framework.dom.ADLExtensibleDOMImplementation;
import org.kite9.framework.xml.ADLDocument;
import org.w3c.dom.Node;


/**
 * Holds XML (either rendered (SVG) or unrendered (ADL) which will be output
 * from other Controllers in the system.  This can then be rendered into a given
 * content-type.  Also handles conversion to DOM Document format and back again if necessary.
 * 
 * @author robmoffat
 *
 */
public class ADLImpl implements ADL {

	private ADLDocument doc;
	private String uri;
	private Kite9SVGTranscoder transcoder = new Kite9SVGTranscoder();

	public ADLImpl(String content, String uri) {
		this.doc = loadXMLDocument(content, uri);
		this.uri = uri;
	}

	public ADLImpl(ADLDocument doc) {
		this.doc = doc;
	}

	@Override
	public String getAsXMLString() {
		return toXMLString(doc);
	}

	public static String toXMLString(Node n) {
		try {
			TransformerFactory factory = TransformerFactory.newInstance();
			Transformer transformer = factory.newTransformer();
			StringWriter sw = new StringWriter();
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
			transformer.transform(new DOMSource(n), new StreamResult(sw));
			return sw.toString();
		} catch (Exception e) {
			throw new Kite9ProcessingException("Couldn't serialize XML:", e);
		}
	}

	@Override
	public ADLDocument getAsDocument() {
		return doc;
	}

	private ADLDocument loadXMLDocument(String xml2, String uri) {
		DocumentFactory f = transcoder.getDocFactory();
		StringReader sr = new StringReader(xml2);
		try {
			ADLDocument document = (ADLDocument) f.createDocument(ADLExtensibleDOMImplementation.SVG_NAMESPACE_URI, SVG12Constants.SVG_SVG_TAG, uri, sr);
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
