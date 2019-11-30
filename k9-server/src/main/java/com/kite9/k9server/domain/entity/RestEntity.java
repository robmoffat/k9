package com.kite9.k9server.domain.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

/**
 * This is used to display information about the object within the admin screens.
 */
public interface RestEntity extends BasicExcerptProjection {
	
	@JsonProperty(access = Access.READ_ONLY)
	RestEntity getParent();

}
