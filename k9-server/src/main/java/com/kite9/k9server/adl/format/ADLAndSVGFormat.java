package com.kite9.k9server.adl.format;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringReader;

import org.springframework.http.MediaType;

import com.kite9.k9server.adl.StreamHelp;
import com.kite9.k9server.adl.holder.ADL;

/**
 * Does nothing as the output format and the input format are the same.
 * 
 * @author robmoffat
 *
 */
public class ADLAndSVGFormat implements Format {

	public MediaType[] getMediaTypes() {
		return new MediaType[] { MediaTypes.ADL_SVG };
	}

	public void handleWrite(ADL data, OutputStream baos, boolean watermark, Integer width, Integer height) throws Exception {
		StreamHelp.streamCopy(new StringReader(data.getAsXMLString()), new OutputStreamWriter(baos), false);
	}



	public String getExtension() {
		return ".png";
	}
	
}