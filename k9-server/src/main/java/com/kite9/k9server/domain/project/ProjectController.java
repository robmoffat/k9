package com.kite9.k9server.domain.project;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.kohsuke.github.GHOrganization;
import org.kohsuke.github.GHPerson;
import org.kohsuke.github.GHPersonSet;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GHUser;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.PagedIterable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.server.LinkBuilder;
import org.springframework.hateoas.server.mvc.BasicLinkBuilder;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kite9.k9server.adl.format.media.Kite9MediaTypes;
import com.kite9.k9server.domain.github.GitHubAPIFactory;
import com.kite9.k9server.domain.github.GithubConfig;


@RestController
public class ProjectController {
	
	@Autowired
	GitHubAPIFactory apiFactory;
	
	
	@GetMapping(path = "/", produces = {
			MediaType.APPLICATION_JSON_VALUE, 
			MediaType.TEXT_HTML_VALUE, 
			MediaTypes.HAL_JSON_VALUE,
			Kite9MediaTypes.SVG_VALUE, 
			Kite9MediaTypes.ADL_SVG_VALUE})
	public User getHomePage(Authentication authentication) throws Exception {
		GitHub github = apiFactory.createApiFor(authentication);
		String name = GithubConfig.getUserLogin(authentication);
		LinkBuilder lb = BasicLinkBuilder.linkToCurrentMapping();
		GHUser user = github.getUser(name);
		
		PagedIterable<GHRepository> repos = user.listRepositories();
		
		List<Repository> repoList = repos.asList().stream()
			.map(r -> {
				Repository p = new Repository() {
			
					@Override
					public String getTitle() {
						return r.getName();
					}
					
					@Override
					public String getDescription() {
						return r.getDescription();
					}
				};
				
				p.add(lb.slash(name).slash(r.getName()).withSelfRel());
				
				return p;
			})
			.collect(Collectors.toList());
		
		GHPersonSet<GHOrganization> orgs = user.getOrganizations();
		
		List<Organisation> orgList = orgs.stream()
			.map(o -> {
				
				String n = safeGetName(o);
				String path = o.getLogin();
				
				Organisation out = new Organisation() {

					@Override
					public String getTitle() {
						return n;
					}

					@Override
					public String getDescription() {
						return "";
					}

					@Override
					public String getIcon() {
						return o.getAvatarUrl();
					}
				};
				
				out.add(lb.slash(name).slash(path).withSelfRel());
				
				return out;
			})
			.collect(Collectors.toList());
		
		User out = new User() {
			
			@Override
			public String getTitle() {
				return safeGetName(user);
			}
			
			@Override
			public String getIcon() {
				return user.getAvatarUrl();
			}
			
			@Override
			public String getDescription() {
				return user.getLogin();
			}
			
			@Override
			public String getCommands() {
				return "";
			}
			
			@Override
			public List<Repository> getRepositories() {
				return repoList;
			}
			
			@Override
			public List<Organisation> getOrganisations() {
				return orgList;
			}
		};
		
		out.add(lb.withSelfRel());
		return out;
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
}
