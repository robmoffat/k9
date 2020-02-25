package com.kite9.k9server.adl.format.media;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.net.URI;

import org.apache.commons.io.Charsets;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.StreamUtils;

import com.kite9.k9server.adl.StreamHelp;
import com.kite9.k9server.adl.holder.ADL;
import com.kite9.k9server.adl.holder.ADLImpl;

/**
 * Outputs the untransformed input xml.
 * 
 * @author robmoffat
 *
 */
public class ADLFormat implements Format {

	public MediaType[] getMediaTypes() {
		return new MediaType[] { Kite9MediaTypes.ADL_SVG, MediaType.TEXT_XML, MediaType.APPLICATION_XML };
	}

	public void handleWrite(ADL data, OutputStream baos, boolean watermark, Integer width, Integer height) throws Exception {
		StreamHelp.streamCopy(new StringReader(data.getAsADLString()), new OutputStreamWriter(baos), false);
	}

	@Override
	public String getExtension() {
		return "adl";
	}

	@Override
	public boolean isBinaryFormat() {
		return false;
	}

	@Override
	public ADL handleRead(InputStream someFormat, URI uri, HttpHeaders headers) throws Exception {
		String s = StreamUtils.copyToString(someFormat, Charsets.UTF_8);
		return ADLImpl.xmlMode(uri, s, headers);
	}

	@Override
	public ADL handleRead(URI in, HttpHeaders headers) {
		return ADLImpl.uriMode(in, headers);
	}

	
}