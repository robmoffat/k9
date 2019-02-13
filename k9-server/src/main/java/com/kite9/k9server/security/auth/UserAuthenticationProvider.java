package com.kite9.k9server.security.auth;

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
		WebSecurityConfig.checkUser(u, true);
		String givenPassword = (String) authentication.getCredentials();
		if (Hash.checkPassword(givenPassword, u.getPassword())) {
			return new UsernamePasswordAuthenticationToken(authentication.getPrincipal(), authentication.getCredentials(), createGrantedAuthorities(u));
		} else {
			throw new BadCredentialsException("Bad Login Credentials");
		}
	}
//
//	private Authentication handleApiKeyAuthentication(Authentication authentication) {
//		Kite9Authentication apiKeyAuthentication = (Kite9Authentication) authentication;
//		User u = userRepository.findByApi((String) apiKeyAuthentication.getCredentials());
//		WebSecurityConfig.checkUser(u, true);
//		return new Kite9Authentication(u, Collections.singletonList(WebSecurityConfig.KITE9_USER));
//	}

	private Collection<? extends GrantedAuthority> createGrantedAuthorities(User u) {
		return Collections.singleton(new SimpleGrantedAuthority("USER"));
	}
}