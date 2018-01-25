package com.kite9.k9server.adl.holder;

import org.springframework.http.MediaType;

/**
 * Holds XML (either rendered (SVG) or unrendered (ADL) which will be output from other Controllers in the system.
 * This can then be rendered into a given content-type.
 * 
 * @author robmoffat
 *
 */
public class ADLImpl implements ADL {
		
	private String xml;
	private final MediaType mt;
	
	public ADLImpl(String content, MediaType mt) {
		this.xml = content;
		this.mt = mt;
	}
	
	@Override
	public String getAsXMLString() {
		return xml;
	}
	
}
