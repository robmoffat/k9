package com.kite9.k9server.adl.format.media;

import java.io.OutputStream;

import org.apache.batik.transcoder.TranscoderOutput;
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

	public void handleWrite(ADL data, OutputStream baos, boolean watermark, Integer width, Integer height) throws Exception {
		Document svg = data.getAsSVGRepresentation();
		Payload.insertEncodedADLInSVG(data, svg);
		data.getTranscoder().writeSVGToOutput(svg, new TranscoderOutput(baos));
	}

	public String getExtension() {
		return "svg";
	}
}