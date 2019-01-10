package com.kite9.k9server.adl.format;

import java.util.Arrays;
import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import com.kite9.k9server.adl.format.media.ADLAndSVGFormat;
import com.kite9.k9server.adl.format.media.Format;
import com.kite9.k9server.adl.format.media.HTMLFormat;
import com.kite9.k9server.adl.format.media.MediaTypes;
import com.kite9.k9server.adl.format.media.PNGFormat;
import com.kite9.k9server.adl.format.media.SVGFormat;

@Component
public class BasicFormatSupplier implements FormatSupplier {
	
	/**
	 * Ordered most specific to least.
	 */
	public static Format[] FORMATS = new Format[] {
			new PNGFormat(),
			new SVGFormat(),
			new ADLAndSVGFormat(),
			new HTMLFormat(),
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
	
	public List<MediaType> getMediaTypes() {
		return Arrays.asList(MediaTypes.ADL_SVG, 
				MediaType.IMAGE_PNG, 
				MediaTypes.SVG, 
				MediaTypes.PDF, 
				MediaType.TEXT_HTML);
	}

}
