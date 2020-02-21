package com.kite9.k9server.domain.github.commands;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.kohsuke.github.GHBranch;
import org.kohsuke.github.GHCommit;
import org.kohsuke.github.GHPerson;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GHTree;
import org.springframework.hateoas.server.LinkBuilder;
import org.springframework.hateoas.server.mvc.BasicLinkBuilder;
import org.springframework.http.HttpStatus;

import com.kite9.k9server.adl.holder.ADL;
import com.kite9.k9server.adl.holder.ADLImpl;
import com.kite9.k9server.command.AbstractGitHubCommand;
import com.kite9.k9server.command.CommandException;
import com.kite9.k9server.domain.github.AbstractGithubController;
import com.kite9.k9server.domain.github.EntityController;
import com.kite9.k9server.domain.github.GitHubAPIFactory;

public class NewDocument extends AbstractGitHubCommand {
	
	public String title;
		
	public String templateUri;
		
	public boolean open = false;	// sends a redirect after creating.
	
	public static final Pattern p = Pattern.compile(
		"^.*(orgs|users)\\/([a-zA-Z-_0-9]+)\\/([a-zA-Z0-9-_]+)(\\/.*)?");
	
	@Override
	public Object applyCommand() throws CommandException {
		try {
			String content = getNewDocumentContent();
			
			// parse out the originating url
			URI u = new URI(subjectUri);
			String pathPart = u.getPath();
			Matcher m = p.matcher(pathPart);
			m.find();
			String type = m.group(1);
			String owner = m.group(2);
			String reponame = m.group(3);
			String path = m.groupCount() >=4 ? m.group(4) : "";
			
			path = path.startsWith("/") ? path.substring(1) : path;
			
			// get the current github details
			GHPerson p = AbstractGithubController.getUserOrOrg(type, owner, github);
			GHRepository repo = p.getRepository(reponame);
			String branchName = repo.getDefaultBranch();
			GHBranch branch = repo.getBranch(branchName);
			GHTree tree = repo.getTree(branchName);
			
			// create content in github
			repo.createBlob().textContent(content).create();
			
			GHTree newTree = repo.createTree()
					.baseTree(tree.getSha())
					.add(path+"/"+title+".kite9.xml", content, false)
					.create();
			
			
			GHCommit c = repo.createCommit()
					.committer(GitHubAPIFactory.getUserLogin(a), GitHubAPIFactory.getEmail(a), new Date())
					.message("Created Kite9 Diagram From Template "+templateUri)
					.parent(branch.getSHA1())
					.tree(newTree.getSha())
					.create();
				
			repo.getRef("heads/"+branchName).updateTo(c.getSHA1());
			LinkBuilder lb = BasicLinkBuilder.linkToCurrentMapping();
			
			return EntityController.templateDirectoryPage(type, owner, reponame, path, p, repo, lb);
		} catch (Exception e) {
			throw new CommandException(HttpStatus.CONFLICT, "Couldn't create document: ", e, this);
		}
	}

	public String getNewDocumentContent() throws URISyntaxException {
		// we need to return ADL which copies the templateUri;
		ADL adl = ADLImpl.uriMode(new URI(templateUri), requestHeaders);
		String content = adl.getAsADLString();
		return content;
	}

}
