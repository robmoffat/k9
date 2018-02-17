package com.kite9.k9server.adl.format;

import java.io.OutputStream;
import java.io.OutputStreamWriter;

import org.apache.batik.transcoder.Transcoder;
import org.apache.batik.transcoder.TranscoderInput;
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
		return new MediaType[] { MediaTypes.SVG };
	}

	public void handleWrite(ADL data, OutputStream baos, boolean watermark, Integer width, Integer height) throws Exception {
		Transcoder transcoder = data.getTranscoder();
		TranscoderInput in = new TranscoderInput(data.getAsDocument());
		TranscoderOutput out = new TranscoderOutput(new OutputStreamWriter(baos));
		transcoder.transcode(in, out);	
	}
	
}