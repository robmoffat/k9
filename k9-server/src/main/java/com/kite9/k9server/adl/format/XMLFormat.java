package com.kite9.k9server.adl.format;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import org.kite9.diagram.adl.Diagram;
import org.kite9.diagram.visualization.display.style.Stylesheet;
import org.kite9.framework.serialization.XMLHelper;
import org.springframework.http.MediaType;

public final class XMLFormat implements Format {
	@Override
	public MediaType[] getMediaTypes() {
		return new MediaType[] { MediaType.TEXT_XML, MediaType.APPLICATION_XML, MediaTypes.ADL_XML };
	}

	@Override
	public void handleWrite(Diagram arrangedDiagram, OutputStream baos,
			Stylesheet ss, boolean watermark,
			Integer width, Integer height) throws IOException {
		
		XMLHelper helper = new XMLHelper();
		String xml = helper.toXML(arrangedDiagram);
		OutputStreamWriter wos1 = new OutputStreamWriter(baos);
		wos1.write(xml);
		wos1.flush();
	}

	@Override
	public String getExtension() {
		return "xml";
	}
}