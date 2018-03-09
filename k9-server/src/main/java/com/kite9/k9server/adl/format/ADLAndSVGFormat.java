package com.kite9.k9server.adl.format;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringReader;

import org.springframework.http.MediaType;

import com.kite9.k9server.adl.StreamHelp;

/**
 * Outputs the untransformed input xml.
 * 
 * @author robmoffat
 *
 */
public class ADLAndSVGFormat implements Format {

	public MediaType[] getMediaTypes() {
		return new MediaType[] { MediaTypes.ADL_SVG };
	}

	public void handleWrite(Formattable data, OutputStream baos, boolean watermark, Integer width, Integer height) throws Exception {
		StreamHelp.streamCopy(new StringReader(data.getInput().getAsXMLString()), new OutputStreamWriter(baos), false);
	}
}