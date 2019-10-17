package com.kite9.k9server.adl.format.media;

import java.io.OutputStream;

import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.kite9.diagram.batik.format.Kite9PNGTranscoder;
import org.kite9.diagram.dom.XMLHelper;
import org.springframework.http.MediaType;
import org.w3c.dom.Document;

import com.kite9.k9server.adl.holder.ADL;

public class PNGFormat implements Format {

	public MediaType[] getMediaTypes() {
		return new MediaType[] { MediaType.IMAGE_PNG };
	}

	/**
	 * This is probably horribly inefficient, as I think lots of resources get loaded twice.
	 */
	public void handleWrite(ADL adl, OutputStream baos,  boolean watermark, Integer width, Integer height) throws Exception {
		Document doc = adl.getAsSVGRepresentation();
		String uri = adl.getUri().toString();
		System.out.println(new XMLHelper().toXML(doc));
		Kite9PNGTranscoder png = new Kite9PNGTranscoder();
		doc.setDocumentURI(uri);
		TranscoderInput in = new TranscoderInput(doc);
		TranscoderOutput out = new TranscoderOutput(baos);
		png.transcode(in, out);
		
	}



	public String getExtension() {
		return "png";
	}
	
}