package com.kite9.k9server.adl.format;

import com.kite9.k9server.adl.holder.ADL;

/**
 * Represents anything that can be output in a different format by the 
 * `FormatSupplier`.
 * 
 * @author robmoffat
 *
 */
public interface Formattable {

	ADL getInput();
	
	String getOutput();
	
	boolean requiresSave();
	
}
