package com.kite9.k9server.adl.holder;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.URI;
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
import org.kite9.framework.common.Kite9XMLProcessingException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.kite9.k9server.adl.format.media.Kite9MediaTypes;

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
		URI, STRING, DOM, PROCESSING
	};

	private Mode mode;
	private URI uri;
	private String xml;
	private Map<String, String> metadata = new HashMap<>();
	private HttpHeaders requestHeaders;
	
	@JsonIgnore
	private ADLDocument adl;
	
	@JsonIgnore
	private Document svg = null;

	@JsonIgnore
	private final Kite9SVGTranscoder transcoder;
	

	
	private ADLImpl(Mode mode, URI uri, String xml, ADLDocument doc, Kite9SVGTranscoder transcoder, HttpHeaders requestHeaders) {
		super();
		this.mode = mode;
		this.uri = uri;
		this.xml = xml;
		this.adl = doc;
		this.transcoder = transcoder == null ? new Kite9SVGTranscoder() : transcoder; 
		this.requestHeaders = requestHeaders;
	}

	public static ADL uriMode(URI uri, HttpHeaders requestHeaders) {
		ADLImpl out = new ADLImpl(Mode.URI, uri, null, null, null, requestHeaders);
		return out;
	}
	
	public static ADL xmlMode(URI uri, String xml, HttpHeaders requestHeaders) {
		ADLImpl out = new ADLImpl(Mode.STRING, uri, xml, null, null, requestHeaders);
		return out;
	}
	
	public static ADL domMode(URI uri, Kite9SVGTranscoder transcoder, ADLDocument doc, HttpHeaders requestHeaders) {
		ADLImpl out = new ADLImpl(Mode.DOM, uri, null, doc, transcoder, requestHeaders);
		return out;		
	}
	
	@Override
	public String getAsADLString() {
		switch (getMode()) {
		case URI:
			xml = loadText(uri);
			mode = Mode.STRING;
			return xml;
		case STRING:
			return xml;
		case DOM:
			xml = toXMLString(adl, false);
			mode = Mode.STRING;
			return xml;
		case PROCESSING:
		default:
			return xml;
		}
	}

	private Mode getMode() {
		return mode;
	}

	@Override
	public ADLDocument getAsADLDocument() {
		switch (getMode()) {
		case URI:
			xml = loadText(uri);
			//$FALL-THROUGH$
		case STRING:
			adl = parseDocument(xml, uri);
			mode = Mode.DOM;
			//$FALL-THROUGH$
		case DOM:
			return adl;
		case PROCESSING:
		default:
			throw new IllegalStateException("Can't return original DOM, it's being processed to svg");
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
			throw new Kite9XMLProcessingException("Couldn't serialize XML:", e, null, null);
		}
	}
	
	public String loadText(URI uri2) {
		try {
			RequestEntity<?> request = new RequestEntity<>(requestADLHeaders(requestHeaders), HttpMethod.GET, uri2);
			RestTemplate template = new RestTemplate();
			ResponseEntity<String> out = template.exchange(request, String.class);
			return out.getBody();
		} catch (Exception e) {
			throw new Kite9XMLProcessingException("Couldn't request XML from: "+uri2, e, null, null);
		}
	}
	
	private HttpHeaders requestADLHeaders(HttpHeaders rh) {
		HttpHeaders headers2 = HttpHeaders.writableHttpHeaders(rh);
		headers2.setAccept(Arrays.asList(Kite9MediaTypes.ADL_SVG));
		return headers2;
	}

	public ADLDocument loadRelatedDocument(URI uri2) {
		String content = loadText(uri2);
		return parseDocument(content, uri2);
	}

	public ADLDocument parseDocument(String content, URI uri2) {
		try {
			Kite9DocumentLoader l = transcoder.getDocLoader();
			InputStream is = new ByteArrayInputStream(content.getBytes());
			return (ADLDocument) l.loadDocument(uri2.toString(), is);
		} catch (Exception e) {
			throw new Kite9XMLProcessingException("Couldn't load XML into DOM, URI: "+uri2, e, content, null);
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
	public void setMeta(String name, String value) {
		metadata.put(name, value);
	}

	@Override
	public Map<String, String> getMetaData() {
		return metadata;
	}
	
	
	public Document getAsSVGRepresentation() throws Kite9ProcessingException {
		switch (getMode()) {
		case URI:
			xml = loadText(uri);
			//$FALL-THROUGH$
		case STRING:
			adl = parseDocument(xml, uri);
			svg = transformADL(adl);
			mode = Mode.PROCESSING;
			return svg;
		case DOM:
			xml = toXMLString(adl, false);
			svg = transformADL(adl);
			mode = Mode.PROCESSING;
			return svg;
		case PROCESSING:
		default:
			return svg;
		}
	}

	protected Document transformADL(Document d) {
		try {
			Transcoder transcoder = getTranscoder();
			TranscoderInput in = new TranscoderInput(d);
			in.setURI(getUri().toString());
			TranscoderOutput out = new TranscoderOutput();
			transcoder.transcode(in, out);
			return out.getDocument();
		} catch (TranscoderException e) {
			throw new Kite9XMLProcessingException("Couldn't get SVG Representation", e, d);
		}
	}

	@Override
	public HttpHeaders getRequestHeaders() {
		return requestHeaders;
	}
}
