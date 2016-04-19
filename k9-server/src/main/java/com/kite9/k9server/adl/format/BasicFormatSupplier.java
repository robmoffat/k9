package com.kite9.k9server.adl.format;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

@Component
public class BasicFormatSupplier implements FormatSupplier {

	public static Format PDF = new PDFFormat();
	public static Format MAP = new ClientSideMapFormat();
	public static Format XML = new XMLFormat();
	public static Format PNG = new PNGFormat();
	public static Format HTML = new HTMLFormat();
	
	/**
	 * Ordered most specific to least.
	 */
	public static Format[] FORMATS = new Format[] {PDF, MAP, PNG, HTML, XML} ;
	
	
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
