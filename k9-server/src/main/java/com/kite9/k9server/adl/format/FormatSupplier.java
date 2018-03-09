package com.kite9.k9server.adl.format;

import java.util.List;

import org.springframework.http.MediaType;

public interface FormatSupplier {

	Format getFormatFor(MediaType mt);
	
	public List<MediaType> getMediaTypes();
}
