package com.kite9.k9server.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.AccountStatusException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import com.kite9.k9server.domain.User;
import com.kite9.k9server.repos.UserRepository;

/**
 * Configuration of security protocols and the url patterns to match them.
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
	UserAuthenticationProvider userAuthenticationProvider;
		
	/**
	 * This login approach handles both form-based and api-key based login, and processes connections using the {@link UserRepository}.
	 * At the moment, there is no caching of login credentials
	 */
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		Kite9ApiKeyBasedAuthenticationFilter kite9ApiFilter = 
				new Kite9ApiKeyBasedAuthenticationFilter(authenticationManager());
		
		http.addFilterAfter(kite9ApiFilter, BasicAuthenticationFilter.class);	//  API-key approach
		http.csrf().disable();
		http.formLogin();
		http.httpBasic();
		http.authorizeRequests()
			.antMatchers("/public/**").permitAll()
			.antMatchers("/api/public/**").permitAll()
			.antMatchers("/**").authenticated();
	}

	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
		auth.authenticationProvider(userAuthenticationProvider);
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
