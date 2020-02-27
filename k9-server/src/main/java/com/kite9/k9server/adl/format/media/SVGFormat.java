package com.kite9.k9server.adl.format.media;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.batik.transcoder.TranscoderOutput;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.w3c.dom.Document;

import com.kite9.k9server.adl.holder.ADL;
import com.kite9.k9server.adl.holder.Payload;

/**
 * Converts ADL to SVG.
 * 
 * @author robmoffat
 *
 */
public class SVGFormat implements Format {

	public MediaType[] getMediaTypes() {
		return new MediaType[] { Kite9MediaTypes.SVG };
	}

	public void handleWrite(ADL data, OutputStream baos, boolean watermark, Integer width, Integer height)
			throws Exception {
		Document svg = data.getAsSVGRepresentation();
		Payload.insertEncodedADLInSVG(data, svg);
		data.getTranscoder().writeSVGToOutput(svg, new TranscoderOutput(baos));
	}

	public String getExtension() {
		return "svg";
	}

	@Override
	public boolean isBinaryFormat() {
		return false;
	}

	@Override
	public ADL handleRead(InputStream someFormat, URI in, HttpHeaders headers) throws Exception {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setValidating(false);
		dbf.setNamespaceAware(true);
		dbf.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
		// an instance of builder to parse the specified xml file
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document doc = db.parse(someFormat);
		return Payload.extractEncodedADLInSVG(in, headers, doc);
	}
}