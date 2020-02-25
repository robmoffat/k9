package com.kite9.k9server.domain.github.revisions;

import java.io.IOException;
import java.util.Base64;
import java.util.Base64.Decoder;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.Charsets;
import org.kite9.framework.common.Kite9ProcessingException;
import org.kohsuke.github.GHBranch;
import org.kohsuke.github.GHCommit;
import org.kohsuke.github.GHCommitQueryBuilder;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GHTree;
import org.kohsuke.github.GHTreeBuilder;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.PagedIterable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.kite9.k9server.adl.format.FormatSupplier;
import com.kite9.k9server.domain.entity.Revision;
import com.kite9.k9server.domain.github.AbstractGithubController;
import com.kite9.k9server.domain.github.GitHubAPIFactory;

@RestController
public class RevisionController extends AbstractGithubController {
	
	public static final Logger LOG = LoggerFactory.getLogger(RevisionController.class);
	
	@Autowired
	GitHubAPIFactory apiFactory;
	
	@Autowired
	FormatSupplier formatSupplier;
	
	@PostMapping(
			path =  {"/{type:users|orgs}/{userorg}/{reponame}/**"}, 
			produces = MediaTypes.HAL_JSON_VALUE)
	public CollectionModel<Revision> update(
			@PathVariable("type") String type, 
			@PathVariable("userorg") String userorg,
			@PathVariable("reponame") String reponame, 
			HttpServletRequest req, 
			@RequestBody Commit details, 
			Authentication a) throws Exception {

		String path = getDirectoryPath(reponame, req);
		GitHub gh = apiFactory.createApiFor(a);
		GHRepository repo = gh.getRepository(userorg+"/"+reponame);
		
		String branchName = repo.getDefaultBranch();
		GHBranch branch = repo.getBranch(branchName);
		GHTree tree = repo.getTree(branchName);
		

		LOG.debug("Before commit");
		
		GHTreeBuilder treeBuilder = repo.createTree().baseTree(tree.getSha());
		Decoder d = Base64.getDecoder();
		
		for (Map.Entry<String, String> file : details.filesToContentBase64.entrySet()) {
			String url = file.getKey();
			String fp = getDirectoryPath(reponame, url);

			boolean binary = formatSupplier.getFormatFor(fp)
				.map(f -> f.isBinaryFormat())
				.orElse(true);
			
			if (binary) {
				byte[] content = d.decode(file.getValue());
				repo.createBlob().binaryContent(content).create();
				treeBuilder.add(fp, content, false);
			} else {
				String content = new String(d.decode(file.getValue()), Charsets.UTF_8);
				repo.createBlob().textContent(content).create();
				treeBuilder.add(fp, content, false);
			}
		}
		
		GHTree newTree = treeBuilder.create();
		Date date = new Date();
		
		GHCommit c = repo.createCommit()
			.committer(
				GitHubAPIFactory.getUserLogin(a),
				GitHubAPIFactory.getEmail(a),
				date)
			.message(details.commitMessage)
			.parent(branch.getSHA1())
			.tree(newTree.getSha())
			.create();
		
		repo.getRef("heads/"+branchName).updateTo(c.getSHA1());
		
		LOG.debug("After Commit");
		
		return queryCommitsToPath(path, repo);
	}
	
	@GetMapping(
			path =  {"/{type:users|orgs}/{userorg}/{reponame}/**"}, 
			produces = MediaType.APPLICATION_JSON_VALUE)
	public CollectionModel<Revision> get(
			@PathVariable("type") String type, 
			@PathVariable("userorg") String userorg,
			@PathVariable("reponame") String reponame, 
			HttpServletRequest req, 
			Authentication a) throws Exception {
		
		String path = getDirectoryPath(reponame, req);
		GitHub gh = apiFactory.createApiFor(a);
		GHRepository repo = gh.getRepository(userorg+"/"+reponame);
		return queryCommitsToPath(path, repo);
	}

	public CollectionModel<Revision> queryCommitsToPath(String path, GHRepository repo) {
		GHCommitQueryBuilder qb = repo.queryCommits();
		PagedIterable<GHCommit> commits = qb.path(path).pageSize(50).list();
		List<Revision> revisions = StreamSupport.stream(commits.spliterator(), false)
			.map(ghc -> convertToRevision(ghc))
			.collect(Collectors.toList());
		
		return new CollectionModel<Revision>(revisions);
	}

	public Revision convertToRevision(GHCommit ghc) {
		try {
			return new Revision(
				ghc.getSHA1(),
				ghc.getCommitDate(),
				ghc.getCommitShortInfo().getCommitter().getName());
		} catch (IOException e) {
			throw new Kite9ProcessingException("Couldn't build revision", e);
		}
	}
}
