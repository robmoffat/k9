package com.kite9.k9server.domain;

/**
 * This is used to display information about the object within the admin screens.
 */
public interface RestEntity extends BasicExcerptProjection {
	
	RestEntity getParent();

}