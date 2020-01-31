package com.kite9.k9server.security;

import java.util.Collection;
import java.util.Collections;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.kite9.k9server.domain.user.User;
import com.kite9.k9server.domain.user.UserRepository;

/**
 * Provides all authentication against our own internal UserRepository.
 * 
 * @author robmoffat
 *
 */
public class UserAuthenticationProvider implements AuthenticationProvider {
	
	public static final String USER_AUTHORITY = "user";
	public static final Collection<GrantedAuthority> USUAL_AUTHORITIES = Collections.singleton(new SimpleGrantedAuthority(USER_AUTHORITY));
	
	private UserRepository userRepository;
	
	public UserAuthenticationProvider(UserRepository userRepository) {
		this.userRepository = userRepository;
	}
	
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
			UsernamePasswordAuthenticationToken out = new UsernamePasswordAuthenticationToken(u.getUsername(), u, createGrantedAuthorities(u));
			out.setDetails(u);
			return out;
		} else {
			throw new BadCredentialsException("Bad Login Credentials");
		}
	}

	private Collection<? extends GrantedAuthority> createGrantedAuthorities(@SuppressWarnings("unused") User u) {
		return USUAL_AUTHORITIES;
	}
}