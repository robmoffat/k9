package com.kite9.k9server.domain.user;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import com.kite9.k9server.command.domain.NewProject;
import com.kite9.k9server.command.domain.Update;
import com.kite9.k9server.command.domain.WithCommands;
import com.kite9.k9server.domain.entity.AbstractLongIdEntity;
import com.kite9.k9server.domain.entity.RestEntity;
import com.kite9.k9server.domain.entity.Secured;
import com.kite9.k9server.domain.entity.Updateable;
import com.kite9.k9server.domain.permission.Member;
import com.kite9.k9server.domain.project.Project;
import com.kite9.k9server.security.Hash;

@Entity
@JsonIgnoreProperties()
@WithCommands({NewProject.class})
public class User extends AbstractLongIdEntity implements UserDetails, Secured, Updateable {

	/**
	 * Users can call themselves anything.  This is the display name.
	 */
	@Column(unique=true, length=100, nullable=false)
	private String displayName;

	/**
	 * In the database, this just stores the hash of the password, using bcrypt.
	 * The client sends the password in this field when creating a user, but 
	 * we never want to send it back.
	 */
	@JsonProperty(access=Access.WRITE_ONLY)
	@Column(length=60)			
	private String password;
	
	/**
	 * Users have to provide a unique email address.  But, we will validate that it belongs to them 
	 * as well. Null is reserved for expired accounts.
	 */
	@Column(unique=true, length=100, nullable=true)
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
	@JsonProperty(access=Access.WRITE_ONLY)
	private String salt = User.createNewSalt();
	
	@OneToMany(fetch = FetchType.LAZY, targetEntity=Member.class, mappedBy = "user", cascade= { CascadeType.REMOVE, CascadeType.REFRESH })
    private List<Member> memberships = new ArrayList<>();
	
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
	
	public User(String displayname, String password, String email) {
		super();
		this.displayName = displayname;
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
		result = prime * result + ((email == null) ? 0 : email.hashCode());
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
		if (email == null) {
			if (other.email != null)
				return false;
		} else if (!email.equals(other.email))
			return false;
		return true;
	}

	public String getUsername() {
		return email;
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
		this.displayName = checkNotNull(newUser.getDisplayName(), this.displayName);
	}

	private <X> X checkNotNull(X possiblyNull, X original) {
		return possiblyNull == null ? original : possiblyNull;
	}

	@Override
	public String toString() {
		return "User [displayname=" + displayName + ", email=" + email + ", id=" + id + "]";
	}

	@JsonIgnore
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return Collections.singleton(new SimpleGrantedAuthority("USER"));
	}

	@JsonIgnore
	@Override
	public boolean isAccountNonExpired() {
		return !isAccountExpired();
	}

	@JsonIgnore
	@Override
	public boolean isAccountNonLocked() {
		return !isAccountLocked();
	}

	@JsonIgnore
	@Override
	public boolean isCredentialsNonExpired() {
		return !isPasswordExpired();
	}

	@JsonIgnore
	@Override
	public boolean isEnabled() {
		return !isAccountExpired();
	}
	
	public List<Member> getMemberships() {
		return memberships;
	}

	@Override
	public String getTitle() {
		return getDisplayName();
	}

	@Override
	public String getDescription() {
		return emailVerified ? "" : "Email Not Verified";
	}

	@Override
	public String getIcon() {
		if (getEmail() == null) {
			return null;
		}
		
		String currentEmailHash = Hash.generateMD5Hash(getEmail().toLowerCase());
		return "https://gravatar.com/avatar/"+currentEmailHash;
	}
	
	@Override
	public Date getLastUpdated() {
		return null;
	}

	@JsonIgnore
	@Override
	public RestEntity getParent() {
		return null;
	}
	
	public boolean checkAccess(Action a) {
		// if the user is new, pass the check
		if (getId() == null) {
			return true;
		}		
		
		String principal = SecurityContextHolder.getContext().getAuthentication().getName();
		
		return email.equals(principal);
	}

	@Override
	public void setTitle(String title) {
		this.displayName = title;
	}

	@Override
	public void setDescription(String description) {
		// tbd
	}
	

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

}
