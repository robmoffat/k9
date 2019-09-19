package com.kite9.k9server.domain;

import java.util.Date;

/**
 * This is used to display information about the object within the admin screens.
 */
public interface Entity {

	String getTitle();
	
	String getDescription();
	
	String getLocalImagePath();
	
	default Date getLastUpdated() {
		return null;
	}

}
