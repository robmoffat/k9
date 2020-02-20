package com.kite9.k9server.command;

import org.kohsuke.github.GitHub;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;

public abstract class AbstractGitHubCommand implements GithubCommand {

	protected String subjectUri;
	protected GitHub github;
	protected HttpHeaders requestHeaders;
	protected Authentication a;
	
	@Override
	public void setGithubApi(GitHub g, HttpHeaders requestHeaders, Authentication a) {
		this.github = g;
		this.requestHeaders = requestHeaders;
		this.a = a;
	}
	
	
}
