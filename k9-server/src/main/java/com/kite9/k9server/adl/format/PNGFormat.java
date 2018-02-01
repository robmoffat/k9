package com.kite9.k9server.adl.format;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringReader;

import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.kite9.diagram.batik.format.Kite9PNGTranscoder;
import org.springframework.http.MediaType;

import com.kite9.k9server.adl.holder.ADL;

public class PNGFormat implements Format {

	public MediaType[] getMediaTypes() {
		return new MediaType[] { MediaType.IMAGE_PNG };
	}

	public void handleWrite(ADL adl, OutputStream baos, boolean watermark, Integer width, Integer height) throws Exception {
		Kite9PNGTranscoder transcoder = new Kite9PNGTranscoder();
		TranscoderInput in = new TranscoderInput(adl.getAsXMLString());
		OutputStreamWriter writer = new OutputStreamWriter(baos);
		TranscoderOutput out = new TranscoderOutput(writer);
		transcoder.transcode(in, out);	
		
	}



	public String getExtension() {
		return ".png";
	}
	
}