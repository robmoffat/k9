package com.kite9.k9server.adl.format;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import org.kite9.diagram.adl.Diagram;
import org.kite9.diagram.visualization.display.style.Stylesheet;
import org.kite9.diagram.visualization.pipeline.rendering.ClientSideMapRenderingPipeline;
import org.springframework.http.MediaType;

public final class ClientSideMapFormat implements Format {

	public MediaType[] getMediaTypes() {
		return new MediaType[] { MediaTypes.CLIENT_SIDE_IMAGE_MAP };
	}

	public String getExtension() {
		return ".map";
	}

	@Override
	public void handleWrite(Diagram arrangedDiagram, OutputStream baos,
			Stylesheet ss, boolean watermark,
			Integer width, Integer height) throws IOException {
		ClientSideMapRenderingPipeline mapPipeline = new ClientSideMapRenderingPipeline();
		String theMap = mapPipeline.render(arrangedDiagram);
		
		
		OutputStreamWriter wos1 = new OutputStreamWriter(baos);
		wos1.write(theMap);
		wos1.flush();
	}
}