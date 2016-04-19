package com.kite9.k9server.adl.format;

import org.springframework.http.MediaType;

public interface FormatSupplier {

	Format getFormatFor(MediaType mt);
}
