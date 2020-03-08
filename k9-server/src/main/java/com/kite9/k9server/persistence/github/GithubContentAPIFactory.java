package com.kite9.k9server.persistence.github;

import static com.kite9.k9server.persistence.github.GithubContentAPI.OWNER;
import static com.kite9.k9server.persistence.github.GithubContentAPI.REPONAME;

import java.io.IOException;

import org.kite9.framework.common.Kite9ProcessingException;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.stereotype.Component;

import com.kite9.k9server.persistence.cache.CacheManagedAPIFactory;
import com.kite9.k9server.persistence.cache.back.VersionedContentAPI;
import com.kite9.k9server.persistence.queue.ChangeQueue;
import com.kite9.k9server.persistence.queue.ChangeQueueImpl;

@Component
public final class GithubContentAPIFactory extends CacheManagedAPIFactory<String> {

	@Autowired
	protected OAuth2AuthorizedClientRepository clientRepository;
	
	protected VersionedContentAPI<String> createVersionedBackingAPI(Authentication a, String path) {
		String token = GithubContentAPI.getOAuthToken(clientRepository, a);
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

	@Override
	protected String getQueueIdentity(Authentication a, String path) {
		return GithubContentAPI.getPathSegment(OWNER, path) + "/"
				+ GithubContentAPI.getPathSegment(REPONAME, path);
	}
	
	@Override
	protected ChangeQueue<String> createChangeQueue(Authentication a, String path) {
		// this limits us to a backlog of 40 commits for a single repo
		return new ChangeQueueImpl<String>(40);
	}

}