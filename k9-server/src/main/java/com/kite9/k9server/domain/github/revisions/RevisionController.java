package com.kite9.k9server.domain.github.revisions;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.util.Base64;
import java.util.Base64.Decoder;
import java.util.Date;
import java.util.List;
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
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.kite9.k9server.adl.format.FormatSupplier;
import com.kite9.k9server.adl.format.media.Format;
import com.kite9.k9server.adl.holder.ADL;
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
			path =  {"/revisions/{userorg}/{reponame}/**"}, 
			produces = MediaTypes.HAL_JSON_VALUE)
	public CollectionModel<Revision> update(
			@PathVariable("userorg") String userorg,
			@PathVariable("reponame") String reponame, 
			@RequestHeader HttpHeaders headers,
			HttpServletRequest req, 
			@RequestBody Commit details, 
			Authentication a) throws Exception {

		URI uri = new URI(req.getRequestURL().toString());
		String path = getDirectoryPath(reponame, req);
		GitHub gh = apiFactory.createApiFor(a);
		GHRepository repo = gh.getRepository(userorg+"/"+reponame);
		
		String branchName = repo.getDefaultBranch();
		GHBranch branch = repo.getBranch(branchName);
		GHTree tree = repo.getTree(branchName);
		

		LOG.debug("Before commit");
		
		GHTreeBuilder treeBuilder = repo.createTree().baseTree(tree.getSha());
		Decoder d = Base64.getDecoder();
		Format f = formatSupplier.getFormatFor(path).orElseThrow();
		
		if (f.isBinaryFormat()) {
			byte[] content = d.decode(details.adlBase64);
			ADL in = formatSupplier.getADLFormat().handleRead(new ByteArrayInputStream(content), uri, headers);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			f.handleWrite(in, baos, false, 0, 0);
			repo.createBlob().binaryContent(baos.toByteArray()).create();
			treeBuilder.add(path, content, false);
		} else if (f == formatSupplier.getADLFormat()) {
			// no conversion to be done
			String content = new String(d.decode(details.adlBase64), Charsets.UTF_8);
			repo.createBlob().textContent(content).create();
			treeBuilder.add(path, content, false);
		} else if (f == formatSupplier.getSVGFormat()) {
			// we already have the svg content sent back to us
			String content = new String(d.decode(details.svgBase64), Charsets.UTF_8);
			repo.createBlob().textContent(content).create();
			treeBuilder.add(path, content, false);
		} else {
			// ok, use the regular conversion approach - text not binary.
			byte[] content = d.decode(details.adlBase64);
			ADL in = formatSupplier.getADLFormat().handleRead(new ByteArrayInputStream(content), uri, headers);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			f.handleWrite(in, baos, false, 0, 0);
			repo.createBlob().textContent(new String(baos.toByteArray(), Charsets.UTF_8)).create();
			treeBuilder.add(path, content, false);
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
		
		return queryCommitsToPath(path, repo, c.getSHA1());
	}
	
	@GetMapping(
			path =  {"/revisions/{userorg}/{reponame}/**"}, 
			produces = MediaTypes.HAL_JSON_VALUE)
	public CollectionModel<Revision> get(
			@PathVariable("userorg") String userorg,
			@PathVariable("reponame") String reponame, 
			HttpServletRequest req, 
			Authentication a) throws Exception {
		
		String path = getDirectoryPath(reponame, req);
		GitHub gh = apiFactory.createApiFor(a);
		GHRepository repo = gh.getRepository(userorg+"/"+reponame);
		return queryCommitsToPath(path, repo, null);
	}

	public CollectionModel<Revision> queryCommitsToPath(String path, GHRepository repo, String latest) {
		GHCommitQueryBuilder qb = repo.queryCommits();
		PagedIterable<GHCommit> commits = qb.path(path).pageSize(50).list();
		String sha1; 
		if (latest == null) {
			GHCommit first = commits.iterator().next();
			sha1 = first.getSHA1();
		} else {
			sha1 = latest;
		}
		
		List<Revision> revisions = StreamSupport.stream(commits.spliterator(), false)
			.map(ghc -> convertToRevision(ghc, ghc.getSHA1().equals(sha1)))
			.collect(Collectors.toList());
		
		return new CollectionModel<Revision>(revisions);
	}

	public Revision convertToRevision(GHCommit ghc, boolean latest) {
		try {
			return new Revision(
				ghc.getSHA1(),
				ghc.getCommitDate(),
				ghc.getCommitShortInfo().getCommitter().getName(), latest);
		} catch (IOException e) {
			throw new Kite9ProcessingException("Couldn't build revision", e);
		}
	}

	public static String getRevisionUrl(String self) {
		return self == null ? null : self.replace("/orgs/", "/revisions/").replace("/users/", "/revisions/");
	}
}
