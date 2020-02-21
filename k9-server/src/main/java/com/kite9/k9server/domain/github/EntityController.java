package com.kite9.k9server.domain.github;

import java.io.IOException;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.kohsuke.github.GHOrganization;
import org.kohsuke.github.GHPerson;
import org.kohsuke.github.GHPersonSet;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GHUser;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.PagedIterable;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.LinkBuilder;
import org.springframework.hateoas.server.mvc.BasicLinkBuilder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.kite9.k9server.domain.entity.Directory;
import com.kite9.k9server.domain.entity.Document;
import com.kite9.k9server.domain.entity.Organisation;
import com.kite9.k9server.domain.entity.Repository;
import com.kite9.k9server.domain.entity.RestEntity;
import com.kite9.k9server.domain.entity.User;


@RestController
public class EntityController extends AbstractGithubController {
		
	@GetMapping(path = "/", produces = MediaType.ALL_VALUE)
	public User getHomePage(Authentication authentication) throws Exception {
		GitHub github = apiFactory.createApiFor(authentication);
		String name = GitHubAPIFactory.getUserLogin(authentication);
		LinkBuilder lb = BasicLinkBuilder.linkToCurrentMapping();
		GHUser user = github.getUser(name);
		
		List<Repository> repoList = templateRepos(lb.slash("/users").slash(name), user);
		List<Organisation> orgList = templateOrganisations(lb.slash("/orgs"), user);
		User out = templateUserOrg(lb.withSelfRel(), user, repoList, orgList);
		return out;
	}


	public static User templateUserOrg(Link self, GHPerson user, List<Repository> repoList, List<Organisation> orgList) {
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
			public List<Repository> getRepositories() {
				return repoList;
			}
			
			@Override
			public List<Organisation> getOrganisations() {
				return orgList;
			}
		};
		
		out.add(self);
		return out;
	}


	public static List<Repository> templateRepos(LinkBuilder lb, GHPerson user) {
		PagedIterable<GHRepository> repos = user.listRepositories();
		
		List<Repository> repoList = repos.asList().stream()
			.map(r -> {
				return templateRepo(lb.slash(r.getName()).withSelfRel(), r);
			})
			.collect(Collectors.toList());
		return repoList;
	}


	public static Repository templateRepo(Link self, GHRepository r) {
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
		
		p.add(self);
		
		return p;
	}


	public static List<Organisation> templateOrganisations(LinkBuilder lb, GHUser user) throws IOException {
		GHPersonSet<GHOrganization> orgs = user.getOrganizations();
		
		List<Organisation> orgList = orgs.stream()
			.map(o -> {
				
				String n = safeGetName(o);
				String path = o.getLogin();
				
				Organisation out = new Organisation() {

					@Override
					public String getTitle() {
						return path;
					}

					@Override
					public String getDescription() {
						return n;
					}

					@Override
					public String getIcon() {
						return o.getAvatarUrl();
					}
				};
				
				out.add(lb.slash(path).withSelfRel());
				
				return out;
			})
			.collect(Collectors.toList());
		return orgList;
	}


	@GetMapping(path = "/{type:users|orgs}/{userorg}", produces = MediaType.ALL_VALUE)
	public User getOrgPage(
			@PathVariable("type") String type, 
			@PathVariable(name = "userorg") String userOrg, 
			Authentication authentication) throws Exception {
		GitHub github = apiFactory.createApiFor(authentication);
		LinkBuilder lb = BasicLinkBuilder.linkToCurrentMapping();
		GHPerson org = getUserOrOrg(type, userOrg, github);
		List<Repository> repoList = templateRepos(lb.slash(type).slash(userOrg), org);
		return templateUserOrg(lb.slash(type).slash(userOrg).withSelfRel(), org, repoList, Collections.emptyList());
	}
	
	

	public static Directory templateDirectory(LinkBuilder lb, GHRepository repo, String path, RestEntity<?> parent, List<Document> contents, List<Directory> subdirectories) {
		Directory out = new Directory() {
			
			@Override
			public String getTitle() {
				return repo.getName();
			}
			
			@Override
			public String getDescription() {
				return repo.getDescription();
			}
			
			@Override
			public List<Document> getDocuments() throws IOException {
				return contents;
			}

			@Override
			public List<Directory> getSubDirectories() throws IOException {
				return subdirectories;
			}

			@Override
			public Date getLastUpdated() {
				// TODO Auto-generated method stub
				return null;
			}


			@Override
			public RestEntity<?> getParent() {
				return parent;
			}
		};
		
		out.add(lb.withSelfRel());
		
		return out;
	}


	@GetMapping(path =  {"/{type:users|orgs}/{userorg}/{reponame}/**",
			"/{type:users|orgs}/{userorg}/{reponame}"}, 
			produces = MediaType.ALL_VALUE)
	public Object getRepoPage(
			@PathVariable("type") String type, 
			@PathVariable("userorg") String userorg,
			@PathVariable("reponame") String reponame, 
			HttpServletRequest req, 
			@RequestHeader HttpHeaders headers,
			Authentication authentication) throws Exception {
		
		String path = getDirectoryPath(reponame, req);

		if (path.endsWith(".kite9.xml")) {
			return getKite9File(authentication, type, userorg, reponame, path, headers, req.getRequestURL().toString());
		} else {
			GitHub github = apiFactory.createApiFor(authentication);
			GHPerson p = getUserOrOrg(type, userorg, github);
			GHRepository repo = p.getRepository(reponame);
			LinkBuilder lb = BasicLinkBuilder.linkToCurrentMapping();	
			return templateDirectoryPage(type, userorg, reponame, path, p, repo, lb); 
		}
	}


	public static Directory templateDirectoryPage(String type, String userorg, String reponame, String path, GHPerson p,
			GHRepository repo, LinkBuilder lb) throws IOException {
		RestEntity<?> parent  = null;
		LinkBuilder userOrgLinkBuilder = lb.slash(type).slash(userorg);
		LinkBuilder repoLinkbuilder = userOrgLinkBuilder.slash(reponame);
		if (path.length() == 0) {
			parent = templateUserOrg(userOrgLinkBuilder.withSelfRel(), p, Collections.emptyList(), Collections.emptyList());
		} else if (path.indexOf("/") == -1) {
			parent = templateRepo(repoLinkbuilder.withSelfRel(), repo);
		} else {
			String parentPath = path.substring(0, path.lastIndexOf("/"));
			parent = templateDirectory(repoLinkbuilder.slash(parentPath), repo, parentPath, parent, null, null);
		}
		
		List<Directory> subDirectories = templateSubDirectories(repoLinkbuilder.slash(path), repo, path);
		List<Document> documents = templateDiagrams(repoLinkbuilder.slash(path), repo, path);
		Directory out = templateDirectory(repoLinkbuilder.slash(path), repo, path, parent, documents, subDirectories);
		return out;
	}


	public static List<Document> templateDiagrams(LinkBuilder lb, GHRepository repo, String path) throws IOException {
		return repo.getDirectoryContent(path).stream()
			.filter(c -> c.isFile())
			.filter(c -> c.getName().endsWith(".kite9.xml"))
			.map(c -> {
				Document out = new Document() {
	
					@Override
					public String getTitle() {
						return c.getName().replace(".kite9.xml", "");
					}
	
					@Override
					public String getDescription() {
						return null;
					}
	
					@Override
					public String getIcon() {
						return c.getUrl().replace(".kite9.xml", ".kite9.svg");
					}
	
					@Override
					public Date getLastUpdated() {
						return null;
					}
	
					@Override
					public String getType() {
						return "document";
					}
	
					@Override
					public String getCommands() {
						return "";
					}
	
					@Override
					public RestEntity<?> getParent() {
						return null;
					}
					
				};
				out.add(lb.slash(c.getName()).withSelfRel());
				return out;
			})
			.collect(Collectors.toList());
	}


	public static List<Directory> templateSubDirectories(LinkBuilder lb, GHRepository repo, String path)
			throws IOException {
		return repo.getDirectoryContent(path).stream()
		.filter(c -> c.isDirectory())
		.map(c -> {
			Directory out = new Directory() {
	
				@Override
				public List<Document> getDocuments() {
					return Collections.emptyList();
				}
	
				@Override
				public List<Directory> getSubDirectories() {
					return Collections.emptyList();
	
				}
	
				@Override
				public String getTitle() {
					return c.getName();
				}
	
				@Override
				public String getDescription() {
					return "";
				}
	
				@Override
				public Date getLastUpdated() {
					return null;
				}
	
				@Override
				public RestEntity<?> getParent() {
					return null;
				}
			};
			
			out.add(lb.slash(c.getName()).withSelfRel());
			return out;
		})
		.collect(Collectors.toList());
	}
}
