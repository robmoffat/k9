package com.kite9.k9server.adl.holder;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URL;
import java.nio.charset.Charset;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.batik.dom.util.DocumentFactory;
import org.apache.batik.transcoder.Transcoder;
import org.apache.batik.util.SVG12Constants;
import org.kite9.diagram.batik.format.Kite9SVGTranscoder;
import org.kite9.diagram.dom.ADLExtensibleDOMImplementation;
import org.kite9.diagram.dom.elements.ADLDocument;
import org.kite9.framework.common.Kite9ProcessingException;
import org.springframework.util.StreamUtils;
import org.w3c.dom.Node;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.kite9.k9server.security.Hash;

/**
 * Holds XML (either rendered (SVG) or unrendered (ADL) which will be output
 * from other Controllers in the system. This can then be rendered into a given
 * content-type. Also handles conversion to DOM Document format and back again
 * if necessary, and loading from a uri, if that's all that's provided.
 * 
 * @author robmoffat
 *
 */
@JsonAutoDetect(fieldVisibility=Visibility.ANY, getterVisibility=Visibility.NONE, isGetterVisibility=Visibility.NONE, setterVisibility=Visibility.NONE)
public class ADLImpl implements ADL {

	enum Mode {
		STRING, DOM, URI
	};

	private String xml;
	private String uri;

	@JsonIgnore
	private ADLDocument doc;
	
	private String xmlHash;
	
	@JsonIgnore
	private Kite9SVGTranscoder transcoder = new Kite9SVGTranscoder();
	
	public ADLImpl() {
	}

	public ADLImpl(String uri) {
		this.uri = uri;
	}

	public ADLImpl(String content, String uri) {
		this.xml = content;
		this.uri = uri;
		this.xmlHash = Hash.generateHash(content);
	}

	@Override
	public String getAsXMLString() {
		if (getMode() == Mode.URI) {
			xml = toXMLString(uri);
			xmlHash = Hash.generateHash(xml);
		} else if (getMode() == Mode.DOM) {
			xml = toXMLString(doc, false);
			xmlHash = Hash.generateHash(xml);
			doc = null;
		}

		return xml;
	}

	private Mode getMode() {
		if (doc != null) {
			return Mode.DOM;
		} else if (xml != null) {
			return Mode.STRING;
		} else {
			return Mode.URI;
		}
	}

	@Override
	public ADLDocument getAsDocument() {
		if (getMode() == Mode.URI) {
			xml = toXMLString(uri);
			xmlHash = Hash.generateHash(xml);
		}
		
		if (getMode() == Mode.STRING) {
			doc = loadXMLDocument(xml, uri);
			xml = null;
		}
		return doc;
	}

	public static String toXMLString(String uri) {
		try {
			InputStream in = new URL(uri).openStream();
			return StreamUtils.copyToString(in, Charset.forName("UTF-8"));
		} catch (IOException e) {
			throw new Kite9ProcessingException("Couldn't get content from: " + uri, e);
		}
	}
	
	public static String toXMLString(Node n, boolean omitDeclaration) {
		try {
			StringWriter output = new StringWriter();
		    Transformer transformer = TransformerFactory.newInstance().newTransformer();
		    if (omitDeclaration) {
		    	transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
		    }
		    transformer.transform(new DOMSource(n), new StreamResult(output));
			return output.toString();
		} catch (Exception e) {
			throw new Kite9ProcessingException("Couldn't serialize XML:", e);
		}
	}

	public ADLDocument loadXMLDocument(String content, String uri2) {
		DocumentFactory f = transcoder.getDocFactory();
		StringReader sr = new StringReader(content);
		try {
			ADLDocument document = (ADLDocument) f.createDocument(ADLExtensibleDOMImplementation.SVG_NAMESPACE_URI, SVG12Constants.SVG_SVG_TAG, uri2, sr);
			document.setDocumentURI(uri2);
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
	public Kite9SVGTranscoder getTranscoder() {
		return transcoder;
	}

	@Override
	public String getAsXMLString(Node n) {
		return toXMLString(n, true);
	}

	@Override
	public String hash(String n) {
		if (n != null) {
			ADLDocument doc = getAsDocument();
			Node e = doc.getElementById(n);
			
			if (e == null) {
				throw new Kite9ProcessingException("Could not locate: "+n);
			}
			
			return Hash.generateHash(e);
		} else {
			if (xmlHash != null) {
				return xmlHash;
			} else if (xml != null) {
				xmlHash = Hash.generateHash(xml);
				return xmlHash;
			}
		}
		
		throw new RuntimeException("XML Hash not set!");
	}

	
}
