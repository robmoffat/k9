package com.kite9.k9server.security;

import java.util.Collections;
import java.util.List;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import com.kite9.k9server.domain.User;

public class ApiKeyAuthentication extends AbstractAuthenticationToken {

	private final String apiKey;
	private final User u;
	private final String name;
	
	public ApiKeyAuthentication(String apiKey) {
		super(Collections.emptyList());
		this.apiKey = apiKey;
		this.u = null;
		this.name = null;
	}
	
	public ApiKeyAuthentication(String apiKey, User u, List<? extends GrantedAuthority> list) {
		super(list);
		this.apiKey = apiKey;
		this.u = u;
		this.name = u.getEmail();
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public Object getCredentials() {
		return apiKey;
	}

	@Override
	public Object getPrincipal() {
		return u;
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
