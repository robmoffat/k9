package com.kite9.k9server.auth;

import org.springframework.test.context.ActiveProfiles;

import com.kite9.k9server.AbstractRestIT;

/**
 * Makes sure there is a new user available to send REST requests with.
 * 
 * @author robmoffat
 *
 */
@ActiveProfiles("credentials")
public abstract class AbstractAuthenticatedIT extends AbstractRestIT {
/*
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
	}*/
}
