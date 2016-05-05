package com.kite9.k9server.adl.format;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import org.kite9.diagram.adl.Diagram;
import org.kite9.diagram.visualization.display.complete.ADLBasicCompleteDisplayer;
import org.kite9.diagram.visualization.display.complete.GriddedCompleteDisplayer;
import org.kite9.diagram.visualization.display.style.Stylesheet;
import org.kite9.diagram.visualization.format.svg.SVGRenderer;
import org.kite9.diagram.visualization.pipeline.rendering.ImageRenderingPipeline;
import org.springframework.http.MediaType;

public class SVGFormat implements Format {

	public MediaType[] getMediaTypes() {
		return new MediaType[] { MediaTypes.SVG };
	}

	public void handleWrite(Diagram arrangedDiagram, OutputStream baos, Stylesheet ss,
			boolean watermark, Integer width, Integer height) throws IOException {
		ImageRenderingPipeline<String> p = new ImageRenderingPipeline<String>(new GriddedCompleteDisplayer(new ADLBasicCompleteDisplayer(ss, watermark, false),ss),
				new SVGRenderer(width, height));
		
		String str = p.render(arrangedDiagram);
		
		if (baos != null) {
			OutputStreamWriter wos1 = new OutputStreamWriter(baos);
			wos1.write(str);
			wos1.flush();
		}
	}



	public String getExtension() {
		return ".png";
	}
	
}