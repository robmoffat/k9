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
	

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (accountExpired ? 1231 : 1237);
		result = prime * result + (accountLocked ? 1231 : 1237);
		result = prime * result + ((api == null) ? 0 : api.hashCode());
		result = prime * result + ((email == null) ? 0 : email.hashCode());
		result = prime * result + (emailVerified ? 1231 : 1237);
		result = prime * result + (emailable ? 1231 : 1237);
		result = prime * result + ((password == null) ? 0 : password.hashCode());
		result = prime * result + (passwordExpired ? 1231 : 1237);
		result = prime * result + ((salt == null) ? 0 : salt.hashCode());
		result = prime * result + ((username == null) ? 0 : username.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		UserResource other = (UserResource) obj;
		if (accountExpired != other.accountExpired)
			return false;
		if (accountLocked != other.accountLocked)
			return false;
		if (api == null) {
			if (other.api != null)
				return false;
		} else if (!api.equals(other.api))
			return false;
		if (email == null) {
			if (other.email != null)
				return false;
		} else if (!email.equals(other.email))
			return false;
		if (emailVerified != other.emailVerified)
			return false;
		if (emailable != other.emailable)
			return false;
		if (password == null) {
			if (other.password != null)
				return false;
		} else if (!password.equals(other.password))
			return false;
		if (passwordExpired != other.passwordExpired)
			return false;
		if (salt == null) {
			if (other.salt != null)
				return false;
		} else if (!salt.equals(other.salt))
			return false;
		if (username == null) {
			if (other.username != null)
				return false;
		} else if (!username.equals(other.username))
			return false;
		return true;
	}

}
