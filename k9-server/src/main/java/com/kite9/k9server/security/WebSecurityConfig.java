package com.kite9.k9server.security;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.AccountStatusException;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import com.kite9.k9server.domain.User;
import com.kite9.k9server.repos.UserRepository;

/**
 * This just enables everything for now.
 * 
 * @author robmoffat
 */
@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
	
	public static final String KITE9_USER_AUTHORITY = "KITE9_USER";

	public static final GrantedAuthority KITE9_USER = new GrantedAuthority() {

		@Override
		public String getAuthority() {
			return KITE9_USER_AUTHORITY;
		}
	};
	
	@Autowired
	UserRepository userRepository;
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		LoginUrlAuthenticationEntryPoint entryPoint = new LoginUrlAuthenticationEntryPoint("/login");
		Kite9ApiKeyBasedAuthenticationFilter kite9ApiFilter = 
				new Kite9ApiKeyBasedAuthenticationFilter(authenticationManager(), entryPoint);
		
		http.addFilterAfter(kite9ApiFilter, BasicAuthenticationFilter.class);	//  API-key approach
		http.csrf().disable();
		http.formLogin();
		http.httpBasic();
		http.authorizeRequests()
			.antMatchers("/api/public/**").permitAll()
			.antMatchers("/**").authenticated();
	}

	/**
	 * This login approach handles both form-based and api-key based login, and processes connections using the {@link UserRepository}.
	 * At the moment, there is no caching of login credentials
	 */
	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
		auth.authenticationProvider(new AuthenticationProvider() {
			
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
				checkUser(u);
				String givenPassword = (String) authentication.getCredentials();
				if (Hash.checkPassword(givenPassword, u.getPassword())) {
					return new UsernamePasswordAuthenticationToken(u, authentication.getCredentials(), Collections.singletonList(KITE9_USER));
				} else {
					throw new BadCredentialsException("Bad Login Credentials");
				}
			}

			private Authentication handleApiKeyAuthentication(Authentication authentication) {
				ApiKeyAuthentication apiKeyAuthentication = (ApiKeyAuthentication) authentication;
				User u = userRepository.findByApi((String) apiKeyAuthentication.getCredentials());
				checkUser(u);
				return new ApiKeyAuthentication((String) apiKeyAuthentication.getCredentials(), u);
			}
			
			
		});
	}
	
	public static void checkUser(User u) throws AccountStatusException {
		if (u == null) {
			throw new UsernameNotFoundException("Unknown User");
		}
		if (u.isAccountExpired()) {
			throw new AccountExpiredException("Account Expired");
		}
		if (u.isAccountLocked()) {
			throw new LockedException("Account Locked");
		}
		if (u.isPasswordExpired()) {
			throw new CredentialsExpiredException("Password Expired");
		}
	}
}
