package com.kite9.k9server.security;

import java.util.Collection;
import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import com.kite9.k9server.domain.user.User;
import com.kite9.k9server.domain.user.UserRepository;

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
		return authentication.equals(UsernamePasswordAuthenticationToken.class);
	}

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		if (authentication instanceof UsernamePasswordAuthenticationToken) {
			return handleFormBasedAuthentication(authentication);
		}
		
		return null;
	}

	private Authentication handleFormBasedAuthentication(Authentication authentication) {
		User u = userRepository.findByEmail(authentication.getName());
		
		if (u == null) {
			u = userRepository.findByUsername(authentication.getName());
		}
		
		WebSecurityConfig.checkUser(u, true);
		String givenPassword = (String) authentication.getCredentials();
		if (Hash.checkPassword(givenPassword, u.getPassword())) {
			return new UsernamePasswordAuthenticationToken(u.getUsername(), authentication.getCredentials(), createGrantedAuthorities(u));
		} else {
			throw new BadCredentialsException("Bad Login Credentials");
		}
	}

	private Collection<? extends GrantedAuthority> createGrantedAuthorities(User u) {
		return Collections.singleton(new SimpleGrantedAuthority("USER"));
	}
}