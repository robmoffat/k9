package com.kite9.k9server.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;


/**
 * Configuration of security protocols and the url patterns to match them.
 * 
 * @author robmoffat
 */
@Configuration
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
	
	/**
	 * This login approach handles both form-based and api-key based login, and
	 * processes connections using the {@link UserRepository}. At the moment,
	 * there is no caching of login credentials
	 */
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.csrf().disable();
		http.headers().frameOptions().disable();
		http.authorizeRequests()
				.antMatchers("/api/command/**").permitAll() 
				.antMatchers("/api/admin").permitAll() 
				.antMatchers("/dist/**").permitAll() // allows rendering tests
				.antMatchers("/api/renderer/**").permitAll() 
				.antMatchers("/api/users/**").permitAll()
				.antMatchers("/api/profile/**").permitAll()
				.antMatchers("/api").permitAll()
				.antMatchers("/stylesheet.js").permitAll()
				.antMatchers("/stylesheet.css").permitAll()
				.antMatchers("/oauth/token").permitAll()
				.antMatchers("/console/**").permitAll()
				.antMatchers("/public/**").permitAll()
				.antMatchers("/examples/**").permitAll()
				.antMatchers("/**").authenticated()
				.and()
				.oauth2Login();
	} 
	
}
