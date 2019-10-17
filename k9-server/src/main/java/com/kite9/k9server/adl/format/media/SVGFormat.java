package com.kite9.k9server.adl.format.media;

import java.io.OutputStream;

import org.apache.batik.transcoder.TranscoderOutput;
import org.springframework.http.MediaType;

import com.kite9.k9server.adl.holder.ADL;

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
		data.getTranscoder().writeSVGToOutput(data.getAsSVGRepresentation(), new TranscoderOutput(baos));
	}

	public String getExtension() {
		return "svg";
	}
}