package com.kite9.k9server.domain.entity;

/**
 * Applies to Documents, Users, Projects
 * 
 * @author robmoffat
 *
 */
public interface Updateable extends RestEntity {

	void setTitle(String title);
	
	void setDescription(String description);

}
