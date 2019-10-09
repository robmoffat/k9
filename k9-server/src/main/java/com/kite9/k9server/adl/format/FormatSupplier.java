package com.kite9.k9server.adl.format;

import java.util.List;
import java.util.Map;

import org.springframework.http.MediaType;

import com.kite9.k9server.adl.format.media.Format;

public interface FormatSupplier {

	Format getFormatFor(MediaType mt);
	
	List<MediaType> getMediaTypes();

	Map<String, MediaType> getMediaTypeMap();
}
