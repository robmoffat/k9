package com.kite9.k9server.domain.user;

import org.springframework.hateoas.RepresentationModel;

/**
 * Sent from the {@link UserController} when some activity has been performed.
 * 
 * @author robmoffat
 *
 */
public class NotificationResource extends RepresentationModel<NotificationResource> {

	private String message;

	public String getMessage() {
		return message;
	}

	public NotificationResource() {
	}
	
	public NotificationResource(String message) {
		super();
		this.message = message;
	}
}
