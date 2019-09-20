package com.kite9.k9server.domain;

import java.util.Date;

/**
 * For collections returned by REST, these fields will always be displayed.
 * 
 * @author robmoffat
 *
 */
public interface BasicExcerptProjection {

	String getTitle();
	
	String getDescription();
	
	String getLocalImagePath();
	
	Date getLastUpdated();
	
	String getType();
}
