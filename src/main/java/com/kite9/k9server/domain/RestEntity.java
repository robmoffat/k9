package com.kite9.k9server.domain;

import java.util.Date;

import org.springframework.hateoas.RepresentationModel;

/**
 * This is used to display information about the object within the admin screens.
 */
public abstract class RestEntity<T> extends RepresentationModel<RestEntity<T>> {
	
	public abstract String getTitle();
	
	public abstract String getDescription();
	
	public abstract String getIcon();
	
	public abstract Date getLastUpdated();
	
	public abstract String getType();
	
	public abstract String getCommands();

	public abstract RestEntity<?> getParent();

}
