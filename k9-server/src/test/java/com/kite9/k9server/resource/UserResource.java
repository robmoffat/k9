package com.kite9.k9server.resource;

import org.springframework.hateoas.ResourceSupport;

public class UserResource extends ResourceSupport {

	public String username;
	public String password;

	public String email;
	public String api;
	public String salt;
	public boolean accountExpired = false;
	public boolean accountLocked = false;
	public boolean passwordExpired = false;
	public boolean emailable = true;
	public boolean emailVerified = false;

	public UserResource() {
	}

	public UserResource(String username, String password, String email, String api) {
		super();
		this.username = username;
		this.password = password;
		this.api = api;
		this.email = email;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

}
