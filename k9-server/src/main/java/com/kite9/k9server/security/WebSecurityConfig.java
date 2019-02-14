package com.kite9.k9server.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.AccountStatusException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.data.repository.query.SecurityEvaluationContextExtension;

import com.kite9.k9server.domain.user.User;
import com.kite9.k9server.domain.user.UserRepository;

/**
 * Configuration of security protocols and the url patterns to match them.
 * 
 * @author robmoffat
 */
@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
	
	@Autowired
	UserAuthenticationProvider userAuthenticationProvider;
		
	/**
	 * This login approach handles both form-based and api-key based login, and processes connections using the {@link UserRepository}.
	 * At the moment, there is no caching of login credentials
	 */
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.formLogin();
		http.httpBasic();
		http.csrf().disable();
		http.headers().frameOptions().disable();
		http.authorizeRequests()
			.antMatchers("/api/command/**").permitAll()	// commands can be used by anyone
			.antMatchers("/dist/**").permitAll()		// allows rendering tests without logging in
			.antMatchers("/api/renderer/**").permitAll()		// allows rendering tests without logging in
			.antMatchers("/api/users/**").permitAll()
			.antMatchers("/api/profile/**").permitAll()
			.antMatchers("/api").permitAll()
			.antMatchers("/stylesheet.js").permitAll()
			.antMatchers("/stylesheet.css").permitAll()
			.antMatchers("/oauth/token").permitAll()
			.antMatchers("/console/**").permitAll()
			.antMatchers("/public/**").permitAll()
			.antMatchers("/examples/**").permitAll()
			.antMatchers("/**").authenticated();
		
	}

	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
		auth.authenticationProvider(userAuthenticationProvider);
	}
	
	public static void checkUser(User u, boolean checkPassword) throws AccountStatusException {
		if (u == null) {
			throw new UsernameNotFoundException("Unknown User");
		}
		if (u.isAccountExpired()) {
			throw new AccountExpiredException("Account Expired");
		}
		if (u.isAccountLocked()) {
			throw new LockedException("Account Locked");
		}
		if (u.isPasswordExpired() && checkPassword) {
			throw new CredentialsExpiredException("Password Expired");
		}
	}
	
	/**
	 * This allows the user id to be used to define which projects/users etc we
	 * can view when we do a "findAll"
	 */
	@Bean
	public SecurityEvaluationContextExtension usePrincipalInQueries() {
		return new SecurityEvaluationContextExtension();
	}
	
}
