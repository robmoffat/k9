package com.kite9.k9server.persistence.github;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.EnumSet;
import java.util.List;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.kite9.framework.common.Kite9ProcessingException;
import org.kohsuke.github.GHCommit;
import org.kohsuke.github.GHContent;
import org.kohsuke.github.GHRef;
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

import com.kite9.k9server.persistence.cache.VersionedContentAPI;

import reactor.core.publisher.Mono;

public abstract class GithubContentAPI implements VersionedContentAPI<String> {

	private Authentication a;
	private String path;
	private String oauthToken;
	private String owner;
	private String reponame;
	private String filepath;
	private String branchName;
	private String tagLast;

	public GithubContentAPI(Authentication a, String path, String oauthToken) {
		this.path = path;
		this.a = a;
		this.oauthToken = oauthToken;
		this.owner = getPathSegment(OWNER, path);
		this.reponame = getPathSegment(REPONAME, path);
		this.filepath = getPathSegment(FILEPATH, path);
		this.branchName = "master";
		this.tagLast = this.filepath != null ? (this.filepath.replaceAll("[^a-zA-Z0-9]", "")+"_last") : null;
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

	private GHRef getLastRef(GHRepository repo) {
		try {
			GHRef ref = repo.getRef("tags/"+tagLast);
			return ref;
		} catch (IOException e) {
			return null;
		}
	}
	
	private GHRef getHeadRef(GHRepository repo) {
		try {
			GHRef ref = repo.getRef("heads/"+branchName);
			return ref;
		} catch (IOException e) {
			return null;
		}
	}

	public String commitRevision(String message, Consumer<GHTreeBuilder> fn) {
		try {
			GHRepository repo = getGitHubAPI().getRepository(owner+"/"+reponame);
			String treeSha;
			String branchSha;
			GHRef lastRef = getLastRef(repo);
			if (lastRef != null) {
				branchSha = lastRef.getObject().getSha();
				treeSha = repo.getTree(branchSha).getSha();
			} else {
				treeSha = repo.getTree(branchName).getSha();
				branchSha = repo.getBranch(branchName).getSHA1();
			}

			GHTreeBuilder treeBuilder = repo.createTree().baseTree(treeSha);
			fn.accept(treeBuilder);
			GHTree newTree = treeBuilder.create();
			
			GHCommit c = repo.createCommit()
					.committer(GithubContentAPI.getUserLogin(a), GithubContentAPI.getEmail(a), new Date())
					.message(message)
					.parent(branchSha)
					.tree(newTree.getSha())
					.create();
			
			repo.getRef("heads/"+branchName).updateTo(c.getSHA1());	
			
			if (lastRef != null) {
				lastRef.delete();
			}
			
			return c.getSHA1();
		} catch (IOException e) {
			throw new Kite9ProcessingException("Couldn't commit change to: "+path, e);
		}
	}

	@Override
	public void commitRevision(byte[] contents, String message) {
		commitRevision(message, tb -> tb.add(filepath, contents, false));
	}

	@Override
	public void commitRevision(String contents, String message) {
		commitRevision(message, tb -> tb.add(filepath, contents, false));
	}

	/**
	 * This has been optimised so that you don't need to build the Github api first
	 */
	@Override
	public InputStream getCurrentRevisionContent() {
		try {
			GHContent content = getGHContent(null);
			return content.read();
		} catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Couldn't load document", e);
		}
	}
	
	@Override
	public InputStream getVersionContent(String k) {
		try {
			GHContent content = getGHContent(k);
			return content.read();
		} catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Couldn't load document", e);
		}
	}

	private GHContent getGHContent(String revision) {
		String uri = "/repos/" + owner+"/"+reponame+"/contents/" + filepath;
		if (revision != null) {
			uri = uri + "?ref="+revision;
		}
		WebClient c = WebClient.create("https://api.github.com");
		Mono<GHContent> mono = c.get().uri(uri)
				.header("Authorization", "token " + oauthToken)
				.retrieve()
				.bodyToMono(GHContent.class);

		GHContent content = mono.block();
		return content;
	}

	public abstract GitHub getGitHubAPI();
	
	public static final Pattern p = Pattern.compile(
			"^.*(orgs|users|content|history)\\/([a-zA-Z-_0-9]+)\\/([a-zA-Z0-9-_]+)(\\/.*)?");
	
	public static final int TYPE = 1;
	public static final int OWNER = 2;
	public static final int REPONAME = 3;
	public static final int FILEPATH = 4;
	
	public static String getPathSegment(int part, String ps) {
		Matcher m = p.matcher(ps);
		if (m.find()) {
			if (part == FILEPATH) {
				String path = m.group(4);
				path = path == null ? "" : path;
				path = path.startsWith("/") ? path.substring(1) : path;
				return path;
			} else {
				return m.group(part);
			}
		} else {
			return null;
		}
	}


	public List<String> getVersionHistory() {
		try {
			GHRepository repo = getGitHubAPI().getRepository(owner+"/"+reponame);
			GHRef lastRef = getLastRef(repo);
			List<String> versions;
			if (lastRef != null) {
				versions = getVersionsForGivenTag(repo, lastRef.getRef());
			} else {
				versions = getVersionsForGivenTag(repo, branchName);
			}
			
			return versions; 
		} catch (IOException e) {
			throw new Kite9ProcessingException("Couldn't retrieve history for "+path, e);
		}
	}

	private List<String> getVersionsForGivenTag(GHRepository repo, String tag) {
		List<String> versions = StreamSupport.stream(
			repo.queryCommits().path(filepath).from(tag).list().spliterator(), false)
			.map(ghc -> ghc.getSHA1())
			.collect(Collectors.toList());
		return versions;
	}

	@Override
	public VersionedContentAPI<String> withPath(String ext) {
		GithubContentAPI me = this;
		return new GithubContentAPI(a, path + ext, oauthToken) {
			
			@Override
			public GitHub getGitHubAPI() {
				return me.getGitHubAPI();
			}
		};
	}

	public String getCurrentVersion() {
		try {
			GHRepository repo = getGitHubAPI().getRepository(owner+"/"+reponame);
			GHRef lastRef = getHeadRef(repo);
			return lastRef.getObject().getSha();
		} catch (IOException e) {
			throw new Kite9ProcessingException("Couldn't retrieve history for "+path, e);
		}		
	}

	public String getOauthToken() {
		return oauthToken;
	}


	public InputStream updateCurrentRevision(String revision) {
		try {
			GHRepository repo = getGitHubAPI().getRepository(owner+"/"+reponame);
			GHRef branchHead = repo.getRef("heads/"+branchName);
			
			// we need to keep track of the latest revision
			GHRef lastRef = getLastRef(repo);
			if (lastRef == null) {
				repo.createRef("refs/tags/"+tagLast, branchHead.getObject().getSha());
			}
			
			branchHead.updateTo(revision, true);	
			return getCurrentRevisionContent();
		} catch (IOException e) {
			throw new Kite9ProcessingException("Couldn't commit change to: "+path, e);
		}
	}
	
	@Override
	public InputStream undo() {
		String current = getCurrentVersion();
		List<String> versions = getVersionHistory();
		int idx = versions.indexOf(current);
		idx = Math.max(0, idx - 1);
		current = versions.get(idx);
		return updateCurrentRevision(current);
	}

	@Override
	public InputStream redo() {
		String current = getCurrentVersion();
		List<String> versions = getVersionHistory();
		int idx = versions.indexOf(current);
		idx = Math.max(0, idx + 1);
		current = versions.get(idx);
		return updateCurrentRevision(current);
	}

	@Override
	public EnumSet<Operation> getOperations() {
		String v = getCurrentVersion();
		List<String> allVersions = getVersionHistory();
		int idx = allVersions.indexOf(v);
		EnumSet<Operation> out = EnumSet.noneOf(Operation.class);
		boolean redo = idx > 0;
		boolean undo = idx < allVersions.size() -1;
		if (redo) {
			out.add(Operation.REDO);
		}
		
		if (undo) {
			out.add(Operation.UNDO);
		}
		
		return out;
	}
	
}
