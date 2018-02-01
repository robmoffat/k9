package com.kite9.k9server.adl.format;

import java.io.OutputStream;

import org.springframework.http.MediaType;

import com.kite9.k9server.adl.holder.ADL;

public final class PDFFormat implements Format {

	public MediaType[] getMediaTypes() {
		return new MediaType[] { MediaTypes.PDF };
	}

	public void handleWrite(ADL xml, OutputStream baos, boolean watermark, Integer width, Integer height) throws Exception {
//		ImageRenderingPipeline<byte[]> p = new ImageRenderingPipeline<byte[]>(new GriddedCompleteDisplayer(new ADLBasicCompleteDisplayer(ss, watermark, false),ss),
//				new PDFRenderer());
//
//		byte[] bi = p.render(arrangedDiagram);
//		if (baos != null) {
//			baos.write(bi);
//			baos.flush();
//		}
	}

	public String getExtension() {
		return ".pdf";
	}
}