package com.kite9.k9server.adl.format;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

@Component
public class BasicFormatSupplier implements FormatSupplier {
	
	/**
	 * Ordered most specific to least.
	 */
	public static Format[] FORMATS = new Format[] {
//			new PDFFormat(),
//			new ClientSideMapFormat(),
//			new PNGFormat(),
			new SVGFormat(),
			new ADLAndSVGFormat(),
//			new HTMLFormat(),
		} ;
	
	
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
