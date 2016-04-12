package com.kite9.k9server.security;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import com.kite9.k9server.domain.User;
import com.kite9.k9server.repos.UserRepository;

/**
 * Provides all authentication against our own internal UserRepository.
 * 
 * @author robmoffat
 *
 */
@Component
public class UserAuthenticationProvider implements AuthenticationProvider {
	
	@Autowired
	UserRepository userRepository;

	
	@Override
	public boolean supports(Class<?> authentication) {
		return authentication.equals(ApiKeyAuthentication.class) || authentication.equals(UsernamePasswordAuthenticationToken.class);
	}

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		if (authentication instanceof ApiKeyAuthentication) {
			return handleApiKeyAuthentication(authentication);
		} else if (authentication instanceof UsernamePasswordAuthenticationToken) {
			return handleFormBasedAuthentication(authentication);
		}
		
		return null;
	}

	private Authentication handleFormBasedAuthentication(Authentication authentication) {
		User u = userRepository.findByEmail(authentication.getName());
		WebSecurityConfig.checkUser(u);
		String givenPassword = (String) authentication.getCredentials();
		if (Hash.checkPassword(givenPassword, u.getPassword())) {
			return new UsernamePasswordAuthenticationToken(u, authentication.getCredentials(), Collections.singletonList(WebSecurityConfig.KITE9_USER));
		} else {
			throw new BadCredentialsException("Bad Login Credentials");
		}
	}

	private Authentication handleApiKeyAuthentication(Authentication authentication) {
		ApiKeyAuthentication apiKeyAuthentication = (ApiKeyAuthentication) authentication;
		User u = userRepository.findByApi((String) apiKeyAuthentication.getCredentials());
		WebSecurityConfig.checkUser(u);
		return new ApiKeyAuthentication((String) apiKeyAuthentication.getCredentials(), u, Collections.singletonList(WebSecurityConfig.KITE9_USER));
	}
}