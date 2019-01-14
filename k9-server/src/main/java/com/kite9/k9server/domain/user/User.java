package com.kite9.k9server.domain.user;

import javax.persistence.Column;
import javax.persistence.Entity;

import com.kite9.k9server.domain.AbstractLongIdEntity;
import com.kite9.k9server.domain.project.Project;
import com.kite9.k9server.security.Hash;

@Entity
public class User extends AbstractLongIdEntity {

	/**
	 * Users can call themselves anything.  We'll use email address to log in.
	 */
	private String username;

	/**
	 * In the database, this just stores the hash of the password, using bcrypt.
	 * However, the client sends the password in this field when creating a user.
	 */
	@Column(length=60)			
	private String password;
	
	/**
	 * Users have to provide a unique email address.  But, we will validate that it belongs to them 
	 * as well.
	 */
	@Column(unique=true, length=70, nullable=false)
	private String email;

	/**
	 * This will be used as an API key, when calling the REST services.
	 */
	@Column(length=32, nullable=false)
	private String api = Project.createRandomString();
	
	/**
	 * Used as a salt for generating password / email reset codes and so on.  
	 * Changed each time we generate a reset code so that codes can't be reused.
	 * This cannot be set externally.
	 */
	@Column(length=10, nullable=false)
	private String salt = User.createNewSalt();
	
	/**
	 * Can be set to expired if the user is deleted, but still has foreign-key references remaining.
	 */
	private boolean accountExpired = false;
	
	/**
	 * Not currently externally controllable.
	 */
	private boolean accountLocked = false;
	
	/**
	 * Would need to be set externally too: means that the user should get an email telling them their password has expired.
	 */
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
	}

	public static String createNewSalt() {
		return Project.createRandomString().substring(0, 10);
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
	
	public String getSalt() {
		return salt;
	}

	public void setSalt(String salt) {
		this.salt = salt;
	}

	public void update(User newUser) {
		this.emailable = newUser.emailable;
		this.emailVerified = email.equals(newUser.getEmail());
		this.email = newUser.getEmail();
		
		if ((newUser.getPassword() != null) && (!newUser.getPassword().equals(password))) {
			this.password = Hash.generatePasswordHash(newUser.getPassword());
			this.passwordExpired = false;
		}
		
		this.api = checkNotNull(newUser.getApi(), this.api);
		this.username = checkNotNull(newUser.getUsername(), this.username);
	}

	private <X> X checkNotNull(X possiblyNull, X original) {
		return possiblyNull == null ? original : possiblyNull;
	}

	@Override
	public String toString() {
		return "User [username=" + username + ", email=" + email + ", id=" + id + "]";
	}
	
	
	
}