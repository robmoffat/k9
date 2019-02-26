package com.kite9.k9server.adl.format.media;

import java.io.OutputStream;

import org.springframework.http.MediaType;

import com.kite9.k9server.adl.holder.ADL;

public class PNGFormat implements Format {

	public MediaType[] getMediaTypes() {
		return new MediaType[] { MediaType.IMAGE_PNG };
	}

	public void handleWrite(ADL adl, OutputStream baos,  boolean watermark, Integer width, Integer height) throws Exception {
//		Kite9PNGTranscoder transcoder = new Kite9PNGTranscoder();
//		TranscoderInput in = new TranscoderInput(new StringReader(adl.getAsXMLString()));
//		TranscoderOutput out = new TranscoderOutput(baos);
//		transcoder.transcode(in, out);	
		throw new UnsupportedOperationException();		// not implemented
	}



	public String getExtension() {
		return ".png";
	}
	
}