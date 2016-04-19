package com.kite9.k9server.adl.format;

import org.springframework.http.MediaType;

public class MediaTypes {

	public static final MediaType SVG = new MediaType("image", "svg+xml");
	public static final MediaType PDF = new MediaType("application", "pdf");
	public static final MediaType ADL_XML = new MediaType("text", "adl+xml");
	public static final MediaType RENDERED_ADL_XML = new MediaType("text", "rendered-adl+xml");
	public static final MediaType CLIENT_SIDE_IMAGE_MAP = new MediaType("text", "html-image-map");
	
}
