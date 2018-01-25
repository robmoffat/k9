package com.kite9.k9server.adl.format;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringReader;

import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.kite9.diagram.batik.format.Kite9SVGTranscoder;
import org.kite9.diagram.batik.format.ResourceReferencer;
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
		return new MediaType[] { MediaTypes.SVG };
	}

	public void handleWrite(ADL data, OutputStream baos, boolean watermark, Integer width, Integer height, ResourceReferencer rr) throws Exception {
		Kite9SVGTranscoder transcoder = new Kite9SVGTranscoder(rr);
		TranscoderInput in = new TranscoderInput(new StringReader(data.getAsXMLString()));
		TranscoderOutput out = new TranscoderOutput(new OutputStreamWriter(baos));
		transcoder.transcode(in, out);	
	}
	
}