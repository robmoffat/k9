package com.kite9.k9server.adl.format;

import java.util.List;

import org.springframework.http.MediaType;

import com.kite9.k9server.adl.format.media.Format;

public interface FormatSupplier {

	Format getFormatFor(MediaType mt);
	
	public List<MediaType> getMediaTypes();
}
