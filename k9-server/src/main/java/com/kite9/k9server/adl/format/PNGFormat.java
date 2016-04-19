package com.kite9.k9server.adl.format;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;

import javax.imageio.ImageIO;

import org.kite9.diagram.adl.Diagram;
import org.kite9.diagram.visualization.display.java2d.GriddedCompleteDisplayer;
import org.kite9.diagram.visualization.display.java2d.adl_basic.ADLBasicCompleteDisplayer;
import org.kite9.diagram.visualization.display.java2d.style.Stylesheet;
import org.kite9.diagram.visualization.format.png.BufferedImageRenderer;
import org.kite9.diagram.visualization.pipeline.rendering.ImageRenderingPipeline;
import org.springframework.http.MediaType;

public class PNGFormat implements Format {

	public MediaType[] getMediaTypes() {
		return new MediaType[] { MediaType.IMAGE_PNG };
	}

	public void handleWrite(Diagram arrangedDiagram, OutputStream baos, Stylesheet ss,
			boolean watermark, Integer width, Integer height) throws IOException {
		ImageRenderingPipeline<BufferedImage> p = new ImageRenderingPipeline<BufferedImage>(new GriddedCompleteDisplayer(new ADLBasicCompleteDisplayer(ss, watermark, false),ss),
				new BufferedImageRenderer(width, height));
		
		BufferedImage bi = p.render(arrangedDiagram);
		
		if (baos != null) {
			ImageIO.write(bi, "PNG", baos);
			baos.flush();
		}
	}



	public String getExtension() {
		return ".png";
	}
	
}