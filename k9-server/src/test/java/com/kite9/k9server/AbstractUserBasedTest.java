package com.kite9.k9server;

import org.junit.Before;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.Authentication;

import com.kite9.k9server.domain.github.GitHubAPIFactory;
import com.kite9.k9server.domain.github.GithubConfig;
import com.kite9.k9server.domain.github.JWTHelper;

/**
 * The authentication object here is a dummy.   
 * By mocking the GithubApiFactory we can use the applications' auth token.
 * 
 * @author robmoffat
 *
 */
public abstract class AbstractUserBasedTest extends AbstractAuthenticatedIT {

	@MockBean
	GitHubAPIFactory apiFactory;
	
	@Autowired
	GithubConfig githubConfig;
	
	@Before
	public void setupAuth() throws Exception {
		Mockito.when(apiFactory.createApiFor(Mockito.any(Authentication.class))).thenAnswer((iom) -> {
			String jwt = JWTHelper.createSignedJwt(githubConfig.pk);
			GitHub github = new GitHubBuilder().withJwtToken(jwt).build();
			return github;
		});
	}
	
}
