package com.kite9.k9server.adl;

import org.springframework.http.MediaType;

/**
 * Holds XML (either rendered or unrendered) which will be output from other Controllers in the system.
 * This can then be rendered into a given content-type.
 * 
 * @author robmoffat
 *
 */
public class ADL {
		
	private final String xml;
	private final MediaType mt;
	
	public ADL(String xml, MediaType mt) {
		this.xml = xml;
		this.mt = mt;
	}

	public String getContent() {
		return xml;
	}

	public MediaType getMediaType() {
		return mt;
	}
	
}
