package com.kite9.k9server.adl.format;

import org.springframework.http.MediaType;

public class MediaTypes {

	public static final String SVG_VALUE = "image/svg+xml";
	public static final String PDF_VALUE = "application/pdf";
	public static final String ADL_XML_VALUE = "text/adl+xml";
	public static final String RENDERED_ADL_XML_VALUE = "text/rendered-adl+xml";
	public static final String CLIENT_SIDE_IMAGE_MAP_VALUE = "text/html-image-map";
	
	public static final MediaType SVG;
	public static final MediaType PDF;
	public static final MediaType ADL_XML;
	public static final MediaType RENDERED_ADL_XML;
	public static final MediaType CLIENT_SIDE_IMAGE_MAP;
	
	static {
		SVG = MediaType.parseMediaType(SVG_VALUE);
		PDF = MediaType.parseMediaType(PDF_VALUE);
		ADL_XML = MediaType.parseMediaType(ADL_XML_VALUE);
		RENDERED_ADL_XML = MediaType.parseMediaType(RENDERED_ADL_XML_VALUE);
		CLIENT_SIDE_IMAGE_MAP = MediaType.parseMediaType(CLIENT_SIDE_IMAGE_MAP_VALUE);
	}
}
