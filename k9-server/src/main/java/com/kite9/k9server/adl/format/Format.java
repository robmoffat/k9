package com.kite9.k9server.adl.format;

import java.io.IOException;
import java.io.OutputStream;

import org.kite9.diagram.adl.Diagram;
import org.kite9.diagram.visualization.display.style.Stylesheet;
import org.springframework.http.MediaType;

/**
 * Handles sending a certain file format to the output stream for http responses.
 * 
 * @author robmoffat
 */
public interface Format {
	
	public MediaType[] getMediaTypes();

	public void handleWrite(Diagram arrangedDiagram, OutputStream baos, Stylesheet ss, boolean watermark, Integer width, Integer height) throws IOException;
	
	public String getExtension();
	
}
