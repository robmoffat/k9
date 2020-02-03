package com.kite9.k9server.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.ClientRegistrationException;
import org.springframework.security.oauth2.provider.client.BaseClientDetails;
import org.springframework.security.oauth2.provider.token.AccessTokenConverter;
import org.springframework.security.oauth2.provider.token.TokenStore;

import com.kite9.k9server.domain.user.User;
import com.kite9.k9server.domain.user.UserRepository;

//@Configuration
//@EnableAuthorizationServer
public class OAuth2AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {
	
	@Autowired
	TokenStore tokenStore;
	
	@Autowired
	AccessTokenConverter accessTokenConverter;
	
	@Autowired
	UserRepository users;
	
	@Override
	public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
		endpoints.tokenStore(tokenStore).accessTokenConverter(accessTokenConverter);
	}

	@Override
	public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
		clients.withClientDetails(clientDetailsService());
	}
	
	public ClientDetailsService clientDetailsService() {
		return new ClientDetailsService() {

			@Override
			public ClientDetails loadClientByClientId(String clientId) throws ClientRegistrationException {
				User u = users.findByEmail(clientId);

				BaseClientDetails out = new BaseClientDetails(clientId, JwtConfig.RESOURCE_ID, JwtConfig.APPLICATION_SCOPE, "client_credentials", UserAuthenticationProvider.USER_AUTHORITY);
				out.setClientSecret(u.getPassword());
				return out;
			}
		};
	}


}