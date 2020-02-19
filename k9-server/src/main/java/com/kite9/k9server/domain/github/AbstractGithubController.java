package com.kite9.k9server.domain.github;

import java.io.IOException;
import java.net.URI;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.Charsets;
import org.kohsuke.github.GHContent;
import org.kohsuke.github.GHPerson;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.util.StreamUtils;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;

import com.kite9.k9server.adl.holder.ADL;
import com.kite9.k9server.adl.holder.ADLImpl;

import reactor.core.publisher.Mono;

public class AbstractGithubController {

	@Autowired
	protected OAuth2AuthorizedClientRepository clientRepository;

	@Autowired
	protected GitHubAPIFactory apiFactory;
	
	public AbstractGithubController() {
		super();
	}

	public GHPerson getUserOrOrg(String type, String userorg, GitHub github) throws IOException {
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

	/**
	 * Since we are in a document, add some meta-data about revisions, and the redo
	 * situation.
	 */
	protected ADL addDocumentMeta(ADL adl, GHContent content) {
		adl.setMeta("sha", content.getSha());
		// adl.setMeta("redo", ""+(r.getNextRevision() != null));
		// adl.setMeta("undo", ""+(r.getPreviousRevision() != null));
		// adl.setMeta("author", r.getAuthor().getEmail());

		// String revisionUrl =
		// entityLinks.linkFor(Revision.class).slash(r.getId()).toString();
		// String documentUrl =
		// entityLinks.linkFor(Document.class).slash(r.getDocument().getId()).toString();

		adl.setMeta(IanaLinkRelations.SELF.value(), adl.getUri().toString());
		return adl;
	}
	
	

	public String getDirectoryPath(String reponame, HttpServletRequest req) {
		String path = req.getRequestURI();
		int after = path.indexOf(reponame);
		after += reponame.length() + 1;
		if (after > path.length()) {
			return "";
		} else {
			return path.substring(after);
		}
	}

	public String safeGetName(GHPerson o) {
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
	public ADL getKite9File(Authentication p,  String type, String userorg, String reponame, String path,
			HttpHeaders headers, String url) {
		try {
			WebClient c = WebClient.create("https://api.github.com");
			Mono<GHContent> mono = c.get()
				.uri("/repos/"+userorg+"/"+reponame+"/contents/"+path)
				.header("Authorization" , "token "+GitHubAPIFactory.getOAuthToken(clientRepository, p))
				.retrieve().bodyToMono(GHContent.class);
			
			GHContent content = mono.block();
			String xml = StreamUtils.copyToString(content.read(), Charsets.UTF_8);
			ADL out = ADLImpl.xmlMode(new URI(url), xml, headers);
			addDocumentMeta(out, content);
			return out;
		} catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Couldn't load document", e);
		}
	}

}