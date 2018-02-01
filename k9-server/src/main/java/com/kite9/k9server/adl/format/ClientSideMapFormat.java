package com.kite9.k9server.adl.format;

import java.io.OutputStream;

import org.springframework.http.MediaType;

import com.kite9.k9server.adl.holder.ADL;

public final class ClientSideMapFormat implements Format {

	public MediaType[] getMediaTypes() {
		return new MediaType[] { MediaTypes.CLIENT_SIDE_IMAGE_MAP };
	}

	public String getExtension() {
		return ".map";
	}

	@Override
	public void handleWrite(ADL xml, OutputStream baos, boolean watermark, Integer width, Integer height) throws Exception {
//		ClientSideMapRenderingPipeline mapPipeline = new ClientSideMapRenderingPipeline();
//		String theMap = mapPipeline.render(arrangedDiagram);
//		
//		
//		OutputStreamWriter wos1 = new OutputStreamWriter(baos);
//		wos1.write(theMap);
//		wos1.flush();
	}
}