package com.kite9.k9server.persistence.github;

import static com.kite9.k9server.persistence.github.GithubContentAPI.OWNER;
import static com.kite9.k9server.persistence.github.GithubContentAPI.REPONAME;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.kite9.framework.common.Kite9ProcessingException;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.stereotype.Component;

import com.kite9.k9server.command.content.ContentAPI;
import com.kite9.k9server.command.content.ContentAPIFactory;
import com.kite9.k9server.persistence.cache.MemoryCachingContentAPI;
import com.kite9.k9server.persistence.cache.VersionedContentAPI;
import com.kite9.k9server.persistence.queue.ChangeQueue;
import com.kite9.k9server.persistence.queue.ChangeQueueImpl;
import com.kite9.k9server.persistence.queue.CommandQueueContentAPI;

@Component
public final class GithubContentAPIFactory implements ContentAPIFactory {

	@Autowired
	OAuth2AuthorizedClientRepository clientRepository;

	private final Map<String, ChangeQueue> changeQueues = new HashMap<String, ChangeQueue>();
	private final Map<String, ContentAPI> cache = new HashMap<String, ContentAPI>();

	@Override
	public ContentAPI createAPI(Authentication a, String path) throws IOException {

		String queueName = GithubContentAPI.getPathSegment(OWNER, path) + "/"
				+ GithubContentAPI.getPathSegment(REPONAME, path);
		
		if (!changeQueues.containsKey(queueName)) {
			changeQueues.putIfAbsent(queueName, new ChangeQueueImpl(15));
		}
		
		ChangeQueue cq = changeQueues.get(queueName);
		String token = GithubContentAPI.getOAuthToken(clientRepository, a);
		
		if (!cache.containsKey(path)) {
			ContentAPI out = buildNewCache(a, path, cq, token);
			cache.put(path, out);
			return out;
		} else {
			ContentAPI out = cache.get(path);
			return out;
		}
	
	}

	private ContentAPI buildNewCache(Authentication a, String path, ChangeQueue cq, String token) {
		VersionedContentAPI<String> backingApi = createGitHubAPI(a, path, token);
		ContentAPI front = createMemoryCachingAPI(backingApi, path);
		return new CommandQueueContentAPI(front, cq, backingApi);
	}

	private ContentAPI createMemoryCachingAPI(VersionedContentAPI<String> with, String path) {
		return new MemoryCachingContentAPI<String>(path, with);
	}

	private VersionedContentAPI<String> createGitHubAPI(Authentication a, String path, String token) {
		return new GithubContentAPI(a, path, token) {

			GitHub gh = null;

			@Override
			public GitHub getGitHubAPI() {
				return getGitHub(token);
			}

			private GitHub getGitHub(String token) {
				if (gh == null) {
					gh = createGitHub(token);
				}

				return gh;
			}

		};
	}

	public GitHub createGitHub(String token) {
		try {
			return new GitHubBuilder().withOAuthToken(token).build();
		} catch (IOException e) {
			throw new Kite9ProcessingException("Couldn't get handle to github", e);
		}
	}

	public GitHub createGithub(Authentication a) {
		String token = GithubContentAPI.getOAuthToken(clientRepository, a);
		return createGitHub(token);
	}
}