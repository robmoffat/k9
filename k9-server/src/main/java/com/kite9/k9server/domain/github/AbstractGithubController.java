package com.kite9.k9server.domain.github;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.http.HttpServletRequest;

import org.kohsuke.github.GHContent;
import org.kohsuke.github.GHPerson;
import org.kohsuke.github.GitHub;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;

import reactor.core.publisher.Mono;

public class AbstractGithubController {

	@Autowired
	protected OAuth2AuthorizedClientRepository clientRepository;

	@Autowired
	protected GitHubAPIFactory apiFactory;
	
	public AbstractGithubController() {
		super();
	}

	public static GHPerson getUserOrOrg(String type, String userorg, GitHub github) throws IOException {
		GHPerson p = null;
		switch (type) {
		case "users":
			p = github.getUser(userorg);
			break;
		case "orgs":
			p = github.getOrganization(userorg);
			break;
		default:
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "type " + type + " not expected");
		}
		return p;
	}
	
	public static String getDirectoryPath(String reponame, HttpServletRequest req) {
		String path = req.getRequestURI();
		return getDirectoryPath(reponame, path);
	}

	public static String getDirectoryPath(String reponame, String path) {
		int after = path.indexOf(reponame);
		after += reponame.length() + 1;
		if (after > path.length()) {
			return "";
		} else {
			return path.substring(after);
		}
	}

	public static String safeGetName(GHPerson o) {
		String n;
		try {
			n = o.getName();
		} catch (IOException e) {
			throw new UnsupportedOperationException("eh?");
		}
		return n;
	}

	/**
	 * This has been optimised so that you don't need to build the Github api first
	 */
	public InputStream getRepositoryInputStream(Authentication p,  String type, String userorg, String reponame, String path,
			HttpHeaders headers, String url, String revisionSha1) {
		try {
			WebClient c = WebClient.create("https://api.github.com");
			Mono<GHContent> mono = c.get()
				.uri("/repos/"+userorg+"/"+reponame+"/contents/"+path)
				.header("Authorization" , "token "+GitHubAPIFactory.getOAuthToken(clientRepository, p))
				.retrieve().bodyToMono(GHContent.class);
			
			GHContent content = mono.block();
			return content.read();
		} catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Couldn't load document", e);
		}
	}

}