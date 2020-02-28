package com.kite9.k9server.github;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.kite9.framework.common.Kite9ProcessingException;
import org.kohsuke.github.GHBranch;
import org.kohsuke.github.GHCommit;
import org.kohsuke.github.GHContent;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GHTree;
import org.kohsuke.github.GHTreeBuilder;
import org.kohsuke.github.GitHub;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;

import com.kite9.k9server.command.content.ContentAPI;

import reactor.core.publisher.Mono;

public abstract class GithubContentAPI implements ContentAPI {

	private Authentication a;
	private String path;
	private String oauthToken;

	public GithubContentAPI(Authentication a, String path, String oauthToken) {
		this.path = path;
		this.a = a;
		this.oauthToken = oauthToken;
	}

	public static String getOAuthToken(OAuth2AuthorizedClientRepository clientRepository, Authentication p) {
		OAuth2AuthorizedClient client = clientRepository.loadAuthorizedClient("github", p, null);
		String token = client.getAccessToken().getTokenValue();
		return token;
	}

	public static String getEmail(Authentication authentication) {
		if (authentication instanceof OAuth2AuthenticationToken) {
			return authentication.getDetails().toString();
		} else {
			throw new UnsupportedOperationException("Couldn't get user email " + authentication);
		}
	}

	public static String getUserLogin(Authentication p) {
		OAuth2User user = (OAuth2User) p.getPrincipal();
		String login = user.getAttribute("login");
		return login;
	}

	@Override
	public InputStream updateCurrentRevision(String revision) {
		try {
			GHRepository repo = getGitHubAPI().getRepository(getPathSegment(OWNER)+"/"+getPathSegment(REPONAME));
			String branchName = repo.getDefaultBranch();
			repo.getRef("heads/"+branchName).updateTo(revision);	
		} catch (IOException e) {
			throw new Kite9ProcessingException("Couldn't commit change to: "+path, e);
		}
	}

	public String commitRevision(String message, Consumer<GHTreeBuilder> fn) {
		try {
			GHRepository repo = getGitHubAPI().getRepository(getPathSegment(OWNER)+"/"+getPathSegment(REPONAME));
			String branchName = repo.getDefaultBranch();
			GHTree tree = repo.getTree(branchName);
			GHBranch branch = repo.getBranch(branchName);
			GHTreeBuilder treeBuilder = repo.createTree().baseTree(tree.getSha());
			fn.accept(treeBuilder);
			GHTree newTree = treeBuilder.create();
			
			
			GHCommit c = repo.createCommit()
					.committer(GithubContentAPI.getUserLogin(a), GithubContentAPI.getEmail(a), new Date())
					.message(message)
					.parent(branch.getSHA1())
					.tree(newTree.getSha())
					.create();
			
			repo.getRef("heads/"+branchName).updateTo(c.getSHA1());	
			
			return c.getSHA1();
		} catch (IOException e) {
			throw new Kite9ProcessingException("Couldn't commit change to: "+path, e);
		}
	}

	@Override
	public String commitRevision(byte[] contents, String message) {
		return commitRevision(message, tb -> tb.add(getPathSegment(FILENAME), contents, false));
	}

	@Override
	public String commitRevision(String contents, String message) {
		return commitRevision(message, tb -> tb.add(getPathSegment(FILENAME), contents, false));
	}

	/**
	 * This has been optimised so that you don't need to build the Github api first
	 */
	@Override
	public InputStream getRevision(String rev) {
		try {
			WebClient c = WebClient.create("https://api.github.com");
			Mono<GHContent> mono = c.get().uri("/repos/" + path)
					.header("Authorization", "token " + oauthToken)
					.retrieve()
					.bodyToMono(GHContent.class);

			GHContent content = mono.block();
			return content.read();
		} catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Couldn't load document", e);
		}
	}

	public abstract GitHub getGitHubAPI();
	
	public static final Pattern p = Pattern.compile(
			"^.*(orgs|users)\\/([a-zA-Z-_0-9]+)\\/([a-zA-Z0-9-_]+)(\\/.*)?");
	
	public static final int TYPE = 1;
	public static final int OWNER = 2;
	public static final int REPONAME = 3;
	public static final int FILENAME = 4;
	
	public String getPathSegment(int part) {
		Matcher m = p.matcher(path);
		m.find();
		if (part == FILENAME) {
			String path = m.group(4);
			path = path == null ? "" : path;
			path = path.startsWith("/") ? path.substring(1) : path;
			path = path.length() > 0 ? path + "/" : path;
			return path;
		} else {
			return m.group(part);
		}
	}

}
