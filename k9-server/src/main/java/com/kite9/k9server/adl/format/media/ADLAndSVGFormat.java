package com.kite9.k9server.adl.format.media;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringReader;

import org.springframework.http.MediaType;

import com.kite9.k9server.adl.StreamHelp;
import com.kite9.k9server.adl.format.Converter;
import com.kite9.k9server.adl.format.formattable.Formattable;

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

	public void handleWrite(Formattable data, OutputStream baos, Converter c, boolean watermark, Integer width, Integer height) throws Exception {
		StreamHelp.streamCopy(new StringReader(data.getInput().getAsXMLString()), new OutputStreamWriter(baos), false);
	}
}