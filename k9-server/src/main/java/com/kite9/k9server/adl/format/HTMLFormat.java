package com.kite9.k9server.adl.format;

import java.io.IOException;
import java.io.OutputStream;

import org.kite9.diagram.adl.Diagram;
import org.kite9.diagram.visualization.display.java2d.style.Stylesheet;
import org.springframework.http.MediaType;

/**
 * Will eventually render the GUI, I guess?  Although, maybe we won't do it this way.
 * 
 * @author robmoffat
 *
 */
public class HTMLFormat implements Format {

	@Override
	public MediaType[] getMediaTypes() {
		return new MediaType[] { MediaType.TEXT_HTML };
	}

	@Override
	public void handleWrite(Diagram arrangedDiagram, OutputStream baos, Stylesheet ss, boolean watermark, Integer width, Integer height) throws IOException {
		baos.write("Hello".getBytes());
	}

	@Override
	public String getExtension() {
		return ".html";
	}

}
