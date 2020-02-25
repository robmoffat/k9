package com.kite9.k9server.command;

import org.kohsuke.github.GitHub;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;

import com.kite9.k9server.adl.format.FormatSupplier;

public abstract class AbstractGitHubCommand implements GithubCommand {

	protected String subjectUri;
	protected transient GitHub github;
	protected transient HttpHeaders requestHeaders;
	protected transient Authentication a;
	protected transient FormatSupplier fs;
	
	@Override
	public void setGithubApi(GitHub g, HttpHeaders requestHeaders, Authentication a, FormatSupplier fs) {
		this.github = g;
		this.requestHeaders = requestHeaders;
		this.a = a;
		this.fs = fs;
	}
	
	
}
