package com.kite9.k9server.adl.format.media;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URL;

import org.kite9.framework.common.Kite9ProcessingException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import com.kite9.k9server.adl.holder.ADL;

/**
 * Handles sending a certain file format to the output stream for http
 * responses.
 * 
 * @author robmoffat
 */
public interface Format {

	public MediaType[] getMediaTypes();

	public void handleWrite(ADL input, OutputStream baos, boolean watermark, Integer width, Integer height)
			throws Exception;

	public String getExtension();

	public boolean isBinaryFormat();

	/**
	 * This knows how to pull back the original ADL from the format.
	 */
	public default ADL handleRead(InputStream someFormat, URI in, HttpHeaders headers) throws Exception {
		throw new UnsupportedOperationException();
		// todo: need to get this from the metadata in the png
	}

	/**
	 * As above, but loads the input stream from the uri.
	 */
	public default ADL handleRead(URI in, HttpHeaders headers) throws Exception {
		InputStream stream;
		try {
			URL url = in.toURL();
			stream = url.openStream();
		} catch (IOException e) {
			throw new Kite9ProcessingException("Couldn't load doc " + in, e);
		}

		return handleRead(stream, in, headers);

	}
}
