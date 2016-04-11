package com.kite9.k9server.security;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationDetailsSource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.Assert;
import org.springframework.web.filter.OncePerRequestFilter;


public class Kite9ApiKeyBasedAuthenticationFilter extends OncePerRequestFilter {
	
	private static final String KITE9_AUTH_PREFIX = "KITE9 ";
	private AuthenticationDetailsSource<HttpServletRequest, ?> authenticationDetailsSource = new WebAuthenticationDetailsSource();
	private AuthenticationManager authenticationManager;
	
	public Kite9ApiKeyBasedAuthenticationFilter(AuthenticationManager authenticationManager) {
		Assert.notNull(authenticationManager, "authenticationManager cannot be null");
		this.authenticationManager = authenticationManager;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
		String header = request.getHeader("Authorization");

		if (header != null && header.startsWith(KITE9_AUTH_PREFIX)) {
			String apiKey = header.substring(KITE9_AUTH_PREFIX.length());
	
			try {
	
				if (authenticationIsRequired(apiKey)) {
					ApiKeyAuthentication authRequest = new ApiKeyAuthentication(apiKey);
					authRequest.setDetails(authenticationDetailsSource.buildDetails(request));
					Authentication authResult = authenticationManager.authenticate(authRequest);
					SecurityContextHolder.getContext().setAuthentication(authResult);
				}
	
			} catch (AuthenticationException failed) {
				SecurityContextHolder.clearContext();
				response.setStatus(401);		// UNAUTHORIZED
				return;
			}
		}

		filterChain.doFilter(request, response);
	}

	
	private boolean authenticationIsRequired(String apiKey) {
		Authentication existingAuth = SecurityContextHolder.getContext().getAuthentication();

		if (existingAuth == null || !existingAuth.isAuthenticated()) {
			return true;
		}

		if (existingAuth instanceof ApiKeyAuthentication
				&& !existingAuth.getName().equals(apiKey)) {
			return true;
		}

		// Handle unusual condition where an AnonymousAuthenticationToken is already
		// present
		// This shouldn't happen very often, as BasicProcessingFitler is meant to be
		// earlier in the filter
		// chain than AnonymousAuthenticationFilter. Nevertheless, presence of both an
		// AnonymousAuthenticationToken
		// together with a BASIC authentication request header should indicate
		// reauthentication using the
		// BASIC protocol is desirable. This behaviour is also consistent with that
		// provided by form and digest,
		// both of which force re-authentication if the respective header is detected (and
		// in doing so replace
		// any existing AnonymousAuthenticationToken). See SEC-610.
		if (existingAuth instanceof AnonymousAuthenticationToken) {
			return true;
		}

		return false;
	}

	@Override
	public void afterPropertiesSet() {
		Assert.notNull(this.authenticationManager, "An AuthenticationManager is required");
	}
}
