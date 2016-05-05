package com.kite9.k9server.adl.format;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

@Component
public class BasicFormatSupplier implements FormatSupplier {

	public static final Format PDF = new PDFFormat();
	public static final Format MAP = new ClientSideMapFormat();
	public static final Format XML = new XMLFormat();
	public static final Format PNG = new PNGFormat();
	public static final Format HTML = new HTMLFormat();
	public static final Format SVG = new SVGFormat();
	public static final Format ADLSVG = new ADLAndSVGFormat();
	
	
	
	/**
	 * Ordered most specific to least.
	 */
	public static Format[] FORMATS = new Format[] {PDF, MAP, PNG, HTML, XML, SVG, ADLSVG} ;
	
	
	@Override
	public Format getFormatFor(MediaType mt) {
		for (Format format : FORMATS) {
			for (MediaType mtProvided : format.getMediaTypes()) {
				if (mtProvided.isCompatibleWith(mt)) {
					return format;
				}
			}
		}
		
		throw new IllegalArgumentException("Format not supported:"+mt);
	}

}
