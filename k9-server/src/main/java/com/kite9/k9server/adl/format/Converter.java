package com.kite9.k9server.adl.format;

import com.kite9.k9server.adl.holder.ADL;

/**
 * Handles conversion to another format, may well involve a cache.
 */
public interface Converter {

	public String getSVGRepresentation(ADL data);
}
