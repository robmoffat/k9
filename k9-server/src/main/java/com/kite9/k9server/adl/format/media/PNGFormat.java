package com.kite9.k9server.adl.format.media;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;

import org.apache.batik.bridge.UserAgent;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.PNGTranscoder;
import org.kite9.diagram.dom.XMLHelper;
import org.springframework.http.HttpHeaders;
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
		PNGTranscoder png = new PNGTranscoder() {

			@Override
			protected UserAgent createUserAgent() {
				return adl.getTranscoder().getUserAgent();
			}
			
		};
		
		png.setTranscodingHints(adl.getTranscoder().getTranscodingHints());
		doc.setDocumentURI(uri);
		TranscoderInput in = new TranscoderInput(doc);
		in.setURI(uri);
		TranscoderOutput out = new TranscoderOutput(baos);
		png.transcode(in, out);
	}



	public String getExtension() {
		return "png";
	}

	@Override
	public boolean isBinaryFormat() {
		return true;
	}

	@Override
	public ADL handleRead(InputStream someFormat, URI in, HttpHeaders headers) throws Exception {
		throw new UnsupportedOperationException();
		// todo: need to get this from the metadata in the png
	}

	@Override
	public ADL handleRead(URI in, HttpHeaders headers) {
		throw new UnsupportedOperationException();
		// todo: need to get this from the metadata in the png
	}
	
}