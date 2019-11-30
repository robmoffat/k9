package com.kite9.k9server.domain.entity;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
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
	
	String getIcon();
	
	Date getLastUpdated();
	
	@JacksonXmlProperty(isAttribute = true)
	String getType();
	
	@JacksonXmlProperty(isAttribute = true)
	String getCommands();
	
	@JacksonXmlProperty(isAttribute = true)
	@JsonProperty(access = Access.READ_ONLY)
	String getLocalId();

}
