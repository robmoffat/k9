package com.kite9.k9server.adl.format.media;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.io.StringWriter;

import org.apache.batik.transcoder.Transcoder;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.kite9.framework.common.Kite9ProcessingException;
import org.springframework.http.MediaType;

import com.kite9.k9server.adl.StreamHelp;
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
		StreamHelp.streamCopy(new StringReader(getSVGRepresentation(data)), new OutputStreamWriter(baos), false);
	}

	public String getSVGRepresentation(ADL data) {
		try {
			Transcoder transcoder = data.getTranscoder();
			TranscoderInput in = new TranscoderInput(data.getAsDocument());
			in.setURI(data.getUri());
			StringWriter sw = new StringWriter();
			TranscoderOutput out = new TranscoderOutput(sw);
			transcoder.transcode(in, out);
			return sw.toString();
		} catch (TranscoderException e) {
			throw new Kite9ProcessingException(e);
		}
	}
}