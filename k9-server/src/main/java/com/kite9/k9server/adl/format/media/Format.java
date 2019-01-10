package com.kite9.k9server.adl.format.media;

import java.io.OutputStream;

import org.springframework.http.MediaType;

import com.kite9.k9server.adl.format.Converter;
import com.kite9.k9server.adl.format.formattable.Formattable;

/**
 * Handles sending a certain file format to the output stream for http responses.
 * 
 * @author robmoffat
 */
public interface Format {
	
	public MediaType[] getMediaTypes();

	public void handleWrite(Formattable input, OutputStream baos, Converter c, boolean watermark, Integer width, Integer height) throws Exception;
	
}
