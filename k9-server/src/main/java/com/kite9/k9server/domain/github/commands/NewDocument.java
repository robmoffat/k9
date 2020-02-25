package com.kite9.k9server.domain.github.commands;

import java.io.ByteArrayOutputStream;
import java.io.StringWriter;
import java.net.URI;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.Charsets;
import org.apache.xmlgraphics.util.WriterOutputStream;
import org.kohsuke.github.GHBlob;
import org.kohsuke.github.GHBranch;
import org.kohsuke.github.GHCommit;
import org.kohsuke.github.GHPerson;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GHTree;
import org.springframework.hateoas.server.LinkBuilder;
import org.springframework.hateoas.server.mvc.BasicLinkBuilder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

import com.kite9.k9server.adl.format.media.Format;
import com.kite9.k9server.adl.holder.ADL;
import com.kite9.k9server.command.AbstractGitHubCommand;
import com.kite9.k9server.command.CommandException;
import com.kite9.k9server.domain.github.AbstractGithubController;
import com.kite9.k9server.domain.github.EntityController;
import com.kite9.k9server.domain.github.GitHubAPIFactory;

public class NewDocument extends AbstractGitHubCommand {
	
	public String title;
		
	public String templateUri;
	
	public String format;
		
	public boolean open = false;	// sends a redirect after creating.
	
	public static final Pattern p = Pattern.compile(
		"^.*(orgs|users)\\/([a-zA-Z-_0-9]+)\\/([a-zA-Z0-9-_]+)(\\/.*)?");
	
	@Override
	public Object applyCommand() throws CommandException {
		try {
			ADL adlContent = getNewDocumentContent(requestHeaders);
			
			// parse out the originating url
			URI u = new URI(subjectUri);
			String pathPart = u.getPath();
			Matcher m = p.matcher(pathPart);
			m.find();
			String type = m.group(1);
			String owner = m.group(2);
			String reponame = m.group(3);
			String path = m.group(4);
			path = path == null ? "" : path;
			path = path.startsWith("/") ? path.substring(1) : path;
			path = path.length() > 0 ? path + "/" : path;
			
			// get the current github details
			GHPerson p = AbstractGithubController.getUserOrOrg(type, owner, github);
			GHRepository repo = p.getRepository(reponame);
			String branchName = repo.getDefaultBranch();
			GHBranch branch = repo.getBranch(branchName);
			GHTree tree = repo.getTree(branchName);

			// work out filename, format
			Format formatter = fs.getFormatFor(format).orElseThrow();
			
			// create blob
			GHBlob blob; 
			if (formatter.isBinaryFormat()) {
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				formatter.handleWrite(adlContent, baos, false, 0, 0);
				blob = repo.createBlob().binaryContent(baos.toByteArray()).create();
			} else {
				StringWriter sw = new StringWriter();
				WriterOutputStream osw = new WriterOutputStream(sw, Charsets.UTF_8.name());
				formatter.handleWrite(adlContent, osw, false, 0, 0);
				blob = repo.createBlob().textContent(sw.toString()).create();
			}
			
			// create tree
			@SuppressWarnings("deprecation")
			GHTree newTree = repo.createTree()
					.baseTree(tree.getSha())
					.shaEntry(path+title+"."+formatter.getExtension(), blob.getSha(), false)
					.create();
			
			
			GHCommit c = repo.createCommit()
					.committer(GitHubAPIFactory.getUserLogin(a), GitHubAPIFactory.getEmail(a), new Date())
					.message("Created Kite9 Diagram From Template "+templateUri)
					.parent(branch.getSHA1())
					.tree(newTree.getSha())
					.create();
				
			repo.getRef("heads/"+branchName).updateTo(c.getSHA1());
			LinkBuilder lb = BasicLinkBuilder.linkToCurrentMapping();
			
			return EntityController.templateDirectoryPage(type, owner, reponame, path, p, repo, lb, fs);
		} catch (Exception e) {
			throw new CommandException(HttpStatus.CONFLICT, "Couldn't create document: ", e, this);
		}
	}

	public ADL getNewDocumentContent(HttpHeaders h) throws Exception {
		// we need to return ADL which copies the templateUri;
		Format f = fs.getFormatFor(templateUri).orElseThrow();
		ADL adl = f.handleRead(new URI(templateUri), h);
		return adl;
	}

}
