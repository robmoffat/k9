package com.kite9.k9server.adl.format;

import org.springframework.http.MediaType;

public class MediaTypes {

	public static final String SVG_VALUE = "image/svg+xml";
	public static final String PDF_VALUE = "application/pdf";
	public static final String ADL_XML_VALUE = "text/adl+xml";
	public static final String ARRANGED_ADL_XML_VALUE = "text/arranged-adl+xml";
	public static final String ADL_SVG_VALUE = "text/adl-svg+xml";
	public static final String CLIENT_SIDE_IMAGE_MAP_VALUE = "text/html-image-map";
	
	public static final MediaType SVG;
	public static final MediaType PDF;
	public static final MediaType ADL_XML;
	public static final MediaType ARRANGED_ADL_XML;
	public static final MediaType ADL_SVG;
	public static final MediaType CLIENT_SIDE_IMAGE_MAP;
	
	static {
		SVG = MediaType.parseMediaType(SVG_VALUE);
		PDF = MediaType.parseMediaType(PDF_VALUE);
		ADL_XML = MediaType.parseMediaType(ADL_XML_VALUE);
		ARRANGED_ADL_XML = MediaType.parseMediaType(ARRANGED_ADL_XML_VALUE);
		ADL_SVG = MediaType.parseMediaType(ADL_SVG_VALUE);
		CLIENT_SIDE_IMAGE_MAP = MediaType.parseMediaType(CLIENT_SIDE_IMAGE_MAP_VALUE);
	}
}
