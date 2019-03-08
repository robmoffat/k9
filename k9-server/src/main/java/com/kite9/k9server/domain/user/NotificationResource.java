package com.kite9.k9server.domain.user;

import org.springframework.hateoas.ResourceSupport;

/**
 * Sent from the {@link UserController} when some activity has been performed.
 * 
 * @author robmoffat
 *
 */
public class NotificationResource extends ResourceSupport {

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
