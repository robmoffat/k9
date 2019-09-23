package com.kite9.k9server.domain;

import java.util.Date;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

/**
 * For collections returned by REST, these fields will always be displayed.
 * 
 * @author robmoffat
 *
 */
public interface BasicExcerptProjection {

	String getTitle();
	
	String getDescription();
	
	@JacksonXmlProperty(isAttribute = true)
	String getLocalImagePath();
	
	@JacksonXmlProperty(isAttribute = true)
	Date getLastUpdated();
	
	@JacksonXmlProperty(isAttribute = true)
	String getType();
}
