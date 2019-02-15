package com.kite9.k9server.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.ResourceServerTokenServices;

/**
 * Allows us to use a JWT token to access apis.
 * 
 * @author robmoffat
 *
 */
//@Configuration
//@EnableResourceServer
//public class OAuth2ResourceServerConfig extends ResourceServerConfigurerAdapter {
//    
//	@Autowired
//	ResourceServerTokenServices tokenServices;
//	
//	@Override
//    public void configure(ResourceServerSecurityConfigurer config) {
//        config.tokenServices(tokenServices);
//    }
//}