package com.kite9.k9server.auth;

import org.junit.Before;
import org.kohsuke.github.GHApp;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestTemplate;

import com.kite9.k9server.AbstractRestIT;
import com.kite9.k9server.github.GithubConfig;
import com.kite9.k9server.github.JWTHelper;

/**
 * Makes sure there is a new user available to send REST requests with.
 * 
 * @author robmoffat
 *
 */
@ActiveProfiles("credentials")
public abstract class AbstractAuthenticatedIT extends AbstractRestIT {

	protected RestTemplate restTemplate;
	protected String authToken = "token abc123";
	
	@Autowired
	GithubConfig ghConfig;
	
	@Value("${github.username}")
	String username;
	
	@Value("${github.password}")
	String password;
	
	@Before
	public void setupRestTemplate() throws Exception {
		this.restTemplate = getRestTemplate();
		//String jwt = JWTHelper.createSignedJwt(ghConfig.pk);
		//System.out.println(jwt);
		//GitHub github = new GitHubBuilder().withPassword(username, password).build();
		//github.		System.out.println(github);
	}
}
