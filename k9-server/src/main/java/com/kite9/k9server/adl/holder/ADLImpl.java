package com.kite9.k9server.adl.holder;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.batik.transcoder.Transcoder;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.kite9.diagram.batik.bridge.Kite9DocumentLoader;
import org.kite9.diagram.batik.format.Kite9SVGTranscoder;
import org.kite9.diagram.dom.elements.ADLDocument;
import org.kite9.framework.common.Kite9ProcessingException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StreamUtils;
import org.springframework.web.client.RestTemplate;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.kite9.k9server.adl.format.media.MediaTypes;
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
	private URI uri;
	
	@JsonIgnore
	private ADLDocument doc;
	
	private String xmlHash;
	
	@JsonIgnore
	private Kite9SVGTranscoder transcoder = new Kite9SVGTranscoder();
	
	private Map<String, String> metadata = new HashMap<>();
	
	private HttpHeaders requestHeaders;

	public ADLImpl(URI uri, HttpHeaders requestHeaders) {
		this.uri = uri;
		this.requestHeaders = requestHeaders;
	}

	public ADLImpl(String content, URI uri, HttpHeaders requestHeaders) {
		this.xml = content;
		this.uri = uri;
		this.xmlHash = Hash.generateSHA1Hash(content);
		this.requestHeaders = requestHeaders;
	}

	@Override
	public String getAsXMLString() {
		if (getMode() == Mode.URI) {
			doc = loadDocument(uri);
		}
		
		if (getMode() == Mode.DOM) {
			xml = toXMLString(doc, false);
			xmlHash = Hash.generateSHA1Hash(xml);
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
			doc = loadDocument(uri);
			xml = null;
			xmlHash = null;
		} else if (getMode() == Mode.STRING) {
			doc = parseDocument(xml, uri);
			xml = null;
		}
		
		return doc;
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
	
	public String loadText(URI uri2) {
		try {
			if (uri2.getScheme().equals("file")) {
				// this is used a lot for testing.
				return StreamUtils.copyToString(uri2.toURL().openStream(), StandardCharsets.UTF_8);
			} else {
				RequestEntity<?> request = new RequestEntity<>(requestADLHeaders(requestHeaders), HttpMethod.GET, uri2);
				RestTemplate template = new RestTemplate();
				ResponseEntity<String> out = template.exchange(request, String.class);
				return out.getBody();
			}
		} catch (Exception e) {
			throw new Kite9ProcessingException("Couldn't request XML from: "+uri2, e);
		}
	}
	
	private HttpHeaders requestADLHeaders(HttpHeaders rh) {
		HttpHeaders headers2 = HttpHeaders.writableHttpHeaders(rh);
		headers2.setAccept(Arrays.asList(MediaTypes.ADL_SVG));
		return headers2;
	}

	public ADLDocument loadDocument(URI uri2) {
		String content = loadText(uri2);
		return parseDocument(content, uri2);
	}

	public ADLDocument parseDocument(String content, URI uri2) {
		try {
			Kite9DocumentLoader l = transcoder.getDocLoader();
			InputStream is = new ByteArrayInputStream(content.getBytes());
			return (ADLDocument) l.loadDocument(uri2.toString(), is);
		} catch (Exception e) {
			throw new Kite9ProcessingException("Couldn't load XML into DOM: ", e);
		}
	}

	@Override
	public URI getUri() {
		return uri;
	}
	
	public void setUri(URI u) {
		this.uri = u;
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
				xmlHash = Hash.generateSHA1Hash(xml);
				return xmlHash;
			}
		}
		
		throw new RuntimeException("XML Hash not set!");
	}

	@Override
	public void setMeta(String name, String value) {
		metadata.put(name, value);
	}

	@Override
	public Map<String, String> getMetaData() {
		return metadata;
	}
	
	private Document svgRepresentation = null;
	
	public Document getSVGRepresentation() throws Kite9ProcessingException {
		if (svgRepresentation == null) {
			try {
				Transcoder transcoder = getTranscoder();
				TranscoderInput in = new TranscoderInput(getAsDocument());
				in.setURI(getUri().toString());
				TranscoderOutput out = new TranscoderOutput();
				transcoder.transcode(in, out);
				svgRepresentation = out.getDocument();
			} catch (TranscoderException e) {
				throw new Kite9ProcessingException(e);
			}
		}
		return svgRepresentation;
	}

	@Override
	public HttpHeaders getHeaders() {
		return requestHeaders;
	}
}
