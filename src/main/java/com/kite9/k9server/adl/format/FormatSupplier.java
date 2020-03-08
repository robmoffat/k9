package com.kite9.k9server.adl.format;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.MediaType;

import com.kite9.k9server.adl.format.media.ADLFormat;
import com.kite9.k9server.adl.format.media.Format;
import com.kite9.k9server.adl.format.media.SVGFormat;

public interface FormatSupplier {

	Format getFormatFor(MediaType mt);
	
	List<MediaType> getMediaTypes();

	Map<String, MediaType> getMediaTypeMap();
	
	Optional<Format> getFormatFor(String path);
	
	ADLFormat getADLFormat();
	
	SVGFormat getSVGFormat();
}
