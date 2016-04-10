package com.kite9.k9server.security;

import java.util.Collection;
import java.util.Collections;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import com.kite9.k9server.domain.User;

public class ApiKeyAuthentication implements Authentication {

	private final String apiKey;
	private final User u;
	private final String name;
	private Object details;
	
	public ApiKeyAuthentication(String apiKey) {
		super();
		this.apiKey = apiKey;
		this.u = null;
		this.name = null;
	}
	
	public ApiKeyAuthentication(String apiKey, User u) {
		super();
		this.apiKey = apiKey;
		this.u = u;
		this.name = u.getEmail();
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return Collections.emptyList();
	}

	@Override
	public Object getCredentials() {
		return apiKey;
	}

	@Override
	public Object getDetails() {
		return details;
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

	public void setDetails(Object buildDetails) {
		this.details = buildDetails;
	}

}
