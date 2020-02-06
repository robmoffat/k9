package com.kite9.k9server.domain.github;

import org.kohsuke.github.GitHub;
import org.springframework.security.core.Authentication;

public interface GitHubAPIFactory {

	GitHub createApi() throws Exception;
	
	GitHub createApiFor(Authentication p) throws Exception;
}
