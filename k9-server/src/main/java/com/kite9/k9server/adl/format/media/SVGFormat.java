package com.kite9.k9server.adl.format.media;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.Base64;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.commons.io.Charsets;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.kite9.k9server.adl.holder.ADL;
import com.kite9.k9server.adl.holder.ADLImpl;
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
		// an instance of builder to parse the specified xml file
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document doc = db.parse(someFormat);

		Element e = doc.getElementById("adl:source");

		String content = e.getTextContent();
		byte[] xml = Base64.getDecoder().decode(content);
		return ADLImpl.xmlMode(in, new String(xml, Charsets.UTF_8), headers);
	}
}