package com.kite9.k9server.adl.format;

import java.io.OutputStream;

import org.kite9.diagram.batik.format.ResourceReferencer;
import org.springframework.http.MediaType;

import com.kite9.k9server.adl.holder.ADL;

public class PNGFormat implements Format {

	public MediaType[] getMediaTypes() {
		return new MediaType[] { MediaType.IMAGE_PNG };
	}

	public void handleWrite(ADL xml, OutputStream baos, boolean watermark, Integer width, Integer height, ResourceReferencer rr) throws Exception {
//		Kite9PNGTranscoder transcoder = new Kite9PNGTranscoder(rr);
//		TranscoderInput in = new TranscoderInput(new StringReader(data.getAsXMLString()));
//		TranscoderOutput out = new TranscoderOutput(new OutputStreamWriter(baos));
//		transcoder.transcode(in, out);	
	}



	public String getExtension() {
		return ".png";
	}
	
}