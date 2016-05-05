package com.kite9.k9server.adl.format;

import java.io.IOException;
import java.io.OutputStream;

import org.kite9.diagram.adl.Diagram;
import org.kite9.diagram.visualization.display.complete.ADLBasicCompleteDisplayer;
import org.kite9.diagram.visualization.display.complete.GriddedCompleteDisplayer;
import org.kite9.diagram.visualization.display.style.Stylesheet;
import org.kite9.diagram.visualization.format.pdf.PDFRenderer;
import org.kite9.diagram.visualization.pipeline.rendering.ImageRenderingPipeline;
import org.springframework.http.MediaType;

public final class PDFFormat implements Format {

	public MediaType[] getMediaTypes() {
		return new MediaType[] { MediaTypes.PDF };
	}

	public void handleWrite(Diagram arrangedDiagram, OutputStream baos,
			Stylesheet ss, boolean watermark, Integer width, Integer height) throws IOException {
		ImageRenderingPipeline<byte[]> p = new ImageRenderingPipeline<byte[]>(new GriddedCompleteDisplayer(new ADLBasicCompleteDisplayer(ss, watermark, false),ss),
				new PDFRenderer());

		byte[] bi = p.render(arrangedDiagram);
		if (baos != null) {
			baos.write(bi);
			baos.flush();
		}
	}

	public String getExtension() {
		return ".pdf";
	}
}