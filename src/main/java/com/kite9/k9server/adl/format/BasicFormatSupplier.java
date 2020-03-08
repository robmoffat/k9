package com.kite9.k9server.adl.format;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import com.kite9.k9server.adl.format.media.ADLFormat;
import com.kite9.k9server.adl.format.media.Format;
import com.kite9.k9server.adl.format.media.HTMLFormat;
import com.kite9.k9server.adl.format.media.PNGFormat;
import com.kite9.k9server.adl.format.media.SVGFormat;

@Component
public class BasicFormatSupplier implements FormatSupplier {
	
	private static final SVGFormat SVG_FORMAT = new SVGFormat();

	private static final ADLFormat ADL_FORMAT = new ADLFormat();

	/**
	 * Ordered most specific to least.
	 */
	public static final Format[] FORMATS = new Format[] {
			new PNGFormat(),
			SVG_FORMAT,
			ADL_FORMAT,
			new HTMLFormat(),
		} ;
	
	public static final MediaType[] MEDIA_TYPES = Arrays.stream(FORMATS)
			.flatMap(f -> Arrays.stream(f.getMediaTypes()))
			.collect(Collectors.toList()).toArray(new MediaType[] {});
	
	public static final String[] MEDIA_TYPE_VALUES = Arrays.stream(MEDIA_TYPES)
			.map(mt -> mt.toString())
			.collect(Collectors.toList()).toArray(new String[] {});

	
	
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
		return Arrays.asList(MEDIA_TYPES);
	}

	@Override
	public Map<String, MediaType> getMediaTypeMap() {
		return Arrays.stream(FORMATS)
			.collect(Collectors.toMap(f -> f.getExtension(), f -> f.getMediaTypes()[0]));
	}

	@Override
	public Optional<Format> getFormatFor(String path) {
		for (Format format : FORMATS) {
			if (path.endsWith(format.getExtension())) {
				return Optional.of(format);
			}
		}
		
		return Optional.empty();
	}

	@Override
	public ADLFormat getADLFormat() {
		return ADL_FORMAT;
	}

	@Override
	public SVGFormat getSVGFormat() {
		return SVG_FORMAT;
	}

	
}
