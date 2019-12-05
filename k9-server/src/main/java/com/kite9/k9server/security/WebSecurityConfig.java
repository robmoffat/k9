package com.kite9.k9server.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.AccountStatusException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.data.repository.query.SecurityEvaluationContextExtension;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationManager;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationProcessingFilter;
import org.springframework.security.oauth2.provider.token.ResourceServerTokenServices;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;

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
	UserRepository users;
	
	@Autowired
	ResourceServerTokenServices tokenServices;
	
	/**
	 * This login approach handles both form-based and api-key based login, and
	 * processes connections using the {@link UserRepository}. At the moment,
	 * there is no caching of login credentials
	 */
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.formLogin().loginPage("/login").permitAll();
		http.httpBasic();
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
		.and().addFilterBefore(jwtAuthFilter(), AbstractPreAuthenticatedProcessingFilter.class);
	}
	
	
	
	
	public OAuth2AuthenticationProcessingFilter jwtAuthFilter() throws Exception {
			OAuth2AuthenticationProcessingFilter resourcesServerFilter = new OAuth2AuthenticationProcessingFilter();
		resourcesServerFilter.setAuthenticationManager(authenticationManager());
		resourcesServerFilter.setStateless(false);
		resourcesServerFilter.afterPropertiesSet();
		
		return resourcesServerFilter;
		
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
	
	@Bean
	public UserDetailsService userDetailsService() {
		return new UserDetailsService() {

			@Override
			public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
				User u = users.findByEmail(username);
				if (u == null) {
					u = users.findByUsername(username);
				}
				
				if (u == null) {
					throw new UsernameNotFoundException("No username / email matching: "+username);
				}

				return u;
			}
		};

	}
	
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth
			.parentAuthenticationManager(oauthAuthenticationManager())
			.authenticationProvider(new UserAuthenticationProvider(users));
	}

	protected AuthenticationManager oauthAuthenticationManager() throws Exception {
		OAuth2AuthenticationManager oauthAuthenticationManager = new OAuth2AuthenticationManager();
		oauthAuthenticationManager.setResourceId(JwtConfig.RESOURCE_ID);
		oauthAuthenticationManager.setTokenServices(tokenServices);
		return oauthAuthenticationManager;
	}

}
