package com.kite9.k9server.security.auth;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import com.kite9.k9server.domain.user.User;
import com.kite9.k9server.domain.user.UserRepository;
import com.kite9.k9server.security.Hash;

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
		return authentication.equals(Kite9Authentication.class) || authentication.equals(UsernamePasswordAuthenticationToken.class);
	}

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		if (authentication instanceof Kite9Authentication) {
			return handleApiKeyAuthentication(authentication);
		} else if (authentication instanceof UsernamePasswordAuthenticationToken) {
			return handleFormBasedAuthentication(authentication);
		}
		
		return null;
	}

	private Authentication handleFormBasedAuthentication(Authentication authentication) {
		User u = userRepository.findByEmail(authentication.getName());
		WebSecurityConfig.checkUser(u, true);
		String givenPassword = (String) authentication.getCredentials();
		if (Hash.checkPassword(givenPassword, u.getPassword())) {
			return new Kite9Authentication(u, Collections.singletonList(WebSecurityConfig.KITE9_USER));
		} else {
			throw new BadCredentialsException("Bad Login Credentials");
		}
	}

	private Authentication handleApiKeyAuthentication(Authentication authentication) {
		Kite9Authentication apiKeyAuthentication = (Kite9Authentication) authentication;
		User u = userRepository.findByApi((String) apiKeyAuthentication.getCredentials());
		WebSecurityConfig.checkUser(u, true);
		return new Kite9Authentication(u, Collections.singletonList(WebSecurityConfig.KITE9_USER));
	}
}