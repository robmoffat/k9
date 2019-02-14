package com.kite9.k9server.security;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.ClientRegistrationException;
import org.springframework.security.oauth2.provider.client.BaseClientDetails;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

import com.kite9.k9server.domain.user.User;
import com.kite9.k9server.domain.user.UserRepository;

@Configuration
public class OAuth2AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {


	
	@Autowired
	UserRepository users;
	
	@Autowired
	UserAuthenticationProvider authProvider;
	
	@Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        endpoints
        //.tokenStore(tokenStore())
//        		.prefix("oauth")
//                 .accessTokenConverter(accessTokenConverter())
                 .authenticationManager(new AuthenticationManager() {
					
					@Override
					public Authentication authenticate(Authentication authentication) throws AuthenticationException {
						return authProvider.authenticate(authentication);
					}
				});
    }
	
	
	
	@Override
	public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
		clients.withClientDetails(new ClientDetailsService() {
			
			@Override
			public ClientDetails loadClientByClientId(String clientId) throws ClientRegistrationException {
				User u = users.findByUsername(clientId);
				
				BaseClientDetails out = new BaseClientDetails(clientId, "blah", "scopes", "grants", "auths");
				out.setClientSecret(u.getPassword());
				return out;
				
			}
		});
	}

	public ClientDetails clientDetails() {
		return new ClientDetails() {

			@Override
			public String getClientId() {
				return "kite9";
			}

			@Override
			public Set<String> getResourceIds() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public boolean isSecretRequired() {
				return false;
			}

			@Override
			public String getClientSecret() {
				return null;
			}

			@Override
			public boolean isScoped() {
				return false;
			}

			@Override
			public Set<String> getScope() {
				return null;
			}

			@Override
			public Set<String> getAuthorizedGrantTypes() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Set<String> getRegisteredRedirectUri() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Collection<GrantedAuthority> getAuthorities() {
				return Collections.singleton(new SimpleGrantedAuthority("Identity"));
			}

			@Override
			public Integer getAccessTokenValiditySeconds() {
				return 10000;
			}

			@Override
			public Integer getRefreshTokenValiditySeconds() {
				return 5000;
			}

			@Override
			public boolean isAutoApprove(String scope) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public Map<String, Object> getAdditionalInformation() {
				// TODO Auto-generated method stub
				return null;
			}
			
		};
	}


	@Bean
    public TokenStore tokenStore() {
        return new JwtTokenStore(accessTokenConverter());
    }
 
    @Bean
    public JwtAccessTokenConverter accessTokenConverter() {
        JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
        converter.setSigningKey("123");
        return converter;
    }
 
    @Bean
    @Primary
    public DefaultTokenServices tokenServices() {
        DefaultTokenServices defaultTokenServices = new DefaultTokenServices();
        defaultTokenServices.setTokenStore(tokenStore());
        defaultTokenServices.setSupportRefreshToken(true);
        return defaultTokenServices;
    }
}