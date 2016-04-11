package com.kite9.k9server.domain;

import javax.persistence.Column;
import javax.persistence.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
public class User extends AbstractLongIdEntity {

	/**
	 * Users can call themselves anything.  We'll use email address to log in.
	 */
	private String username;

	@JsonIgnore
	@Column(length=60)			// long enough for BCrypted password
	private String password;
	
	/**
	 * Users have to provide a unique email address.  But, we will validate that it belongs to them 
	 * as well.
	 */
	@JsonIgnore
	@Column(unique=true, length=70, nullable=false)
	private String email;

	/**
	 * This will be used as an API key, when calling the REST services.
	 */
	@Column(length=32, nullable=false)
	private String api;
	
	private boolean accountExpired = false;
	private boolean accountLocked = false;
	private boolean passwordExpired = false;
	private boolean emailable = true;
	private boolean emailVerified=false;

	public User() {
	}
	
	public User(String username, String password, String email) {
		super();
		this.username = username;
		this.password = password;
		this.email = email;
		this.api = Project.createRandomString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
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
		User other = (User) obj;
		if (username == null) {
			if (other.username != null)
				return false;
		} else if (!username.equals(other.username))
			return false;
		return true;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public boolean isAccountExpired() {
		return accountExpired;
	}

	public void setAccountExpired(boolean accountExpired) {
		this.accountExpired = accountExpired;
	}

	public boolean isAccountLocked() {
		return accountLocked;
	}

	public void setAccountLocked(boolean accountLocked) {
		this.accountLocked = accountLocked;
	}

	public boolean isPasswordExpired() {
		return passwordExpired;
	}

	public void setPasswordExpired(boolean passwordExpired) {
		this.passwordExpired = passwordExpired;
	}

	public boolean isEmailable() {
		return emailable;
	}

	public void setEmailable(boolean emailable) {
		this.emailable = emailable;
	}

	public boolean isEmailVerified() {
		return emailVerified;
	}

	public void setEmailVerified(boolean emailVerified) {
		this.emailVerified = emailVerified;
	}

	public String getApi() {
		return api;
	}

	public void setApi(String api) {
		this.api = api;
	}
	
	
}
