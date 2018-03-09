package com.kite9.k9server.security.auth;

import java.util.Collections;
import java.util.List;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import com.kite9.k9server.domain.user.User;

public class Kite9Authentication extends AbstractAuthenticationToken {

	private final String apiKey;
	private final User u;
	private final String email;
	
	public Kite9Authentication(String apiKey) {
		super(Collections.emptyList());
		this.apiKey = apiKey;
		this.u = null;
		this.email = null;
	}
	
	public Kite9Authentication(User u, List<? extends GrantedAuthority> list) {
		super(list);
		this.apiKey = u.getApi();
		this.u = u;
		this.email = u.getEmail();
	}

	@Override
	public String getName() {
		return email;
	}
	
	public String getEmail() {
		return email;
	}

	@Override
	public Object getCredentials() {
		return apiKey;
	}

	@Override
	public Object getPrincipal() {
		return email;
	}
	
	@Override
	public boolean isAuthenticated() {
		return u != null;
	}

	@Override
	public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
		throw new IllegalArgumentException("This setter shouldn't be used - create a new object with a user");
	}

}
