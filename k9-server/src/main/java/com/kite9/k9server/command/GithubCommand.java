package com.kite9.k9server.command;

import org.kohsuke.github.GitHub;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;

import com.kite9.k9server.adl.format.FormatSupplier;

/**
 * Encapsulates the commands are often applied to a subject, e.g. a particular 
 * document or user.
 * 
 * @author robmoffat
 *
 */
public interface GithubCommand extends Command {
	
	public void setGithubApi(GitHub g, HttpHeaders h, Authentication a, FormatSupplier fs);
}
