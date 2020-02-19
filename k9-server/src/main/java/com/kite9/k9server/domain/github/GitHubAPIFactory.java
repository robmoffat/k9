package com.kite9.k9server.domain.github;

import org.kohsuke.github.GitHub;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.core.user.OAuth2User;

public interface GitHubAPIFactory {
	
	GitHub createApiFor(Authentication p) throws Exception;

	static String getOAuthToken(OAuth2AuthorizedClientRepository clientRepository, Authentication p) {
		OAuth2AuthorizedClient client = clientRepository.loadAuthorizedClient("github", p, null);
		String token = client.getAccessToken().getTokenValue();
		return token;
	}

	static String getEmail(Authentication authentication) {
		if (authentication instanceof OAuth2AuthenticationToken) {
			return authentication.getDetails().toString();
		} else {
			throw new UnsupportedOperationException("Couldn't get user email "+authentication);
		}
	}

	static String getUserLogin(Authentication p) {
		OAuth2User user = (OAuth2User) p.getPrincipal();
		String login = user.getAttribute("login");
		return login;
	}
}
