package com.kite9.k9server.command.controllers;

import java.net.URI;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.kite9.diagram.dom.XMLHelper;
import org.kite9.framework.logging.Logable;
import org.kohsuke.github.GHBlob;
import org.kohsuke.github.GHBlobBuilder;
import org.kohsuke.github.GHBranch;
import org.kohsuke.github.GHCommit;
import org.kohsuke.github.GHCommitBuilder;
import org.kohsuke.github.GHPerson;
import org.kohsuke.github.GHRef;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GHTree;
import org.kohsuke.github.GitHub;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.kite9.k9server.adl.format.media.Kite9MediaTypes;
import com.kite9.k9server.adl.holder.ADL;
import com.kite9.k9server.command.Command;
import com.kite9.k9server.command.CommandException;
import com.kite9.k9server.command.XMLCommand;
import com.kite9.k9server.domain.entity.Document;
import com.kite9.k9server.domain.github.GitHubAPIFactory;

/**
 * Accepts commands to the system in order to modify XML.  Contents are returned back in whatever format is
 * requested.  For /content POST.
 * 
 * @author robmoffat
 *
 */
@RestController
public class ContentCommandController extends AbstractCommandController implements Logable {
		
	/**
	 * This is used for applying commands to domain objects.
	 */
	@RequestMapping(method={RequestMethod.POST}, 
		path = "/{type:users|orgs}/{userorg}/{reponame}/**", 
		consumes= {MediaType.APPLICATION_JSON_VALUE},
		
		produces= {
			MediaTypes.HAL_JSON_VALUE, 
			Kite9MediaTypes.ADL_SVG_VALUE, 
			Kite9MediaTypes.SVG_VALUE
		}) 
	public ADL applyCommandOnResource (
			@PathVariable("type") String type, 
			@PathVariable("userorg") String userorg, 
			@PathVariable("reponame") String reponame, 
			HttpServletRequest req,
			Authentication authentication,
			@RequestHeader HttpHeaders headers,
			@RequestBody List<Command> request) throws CommandException {
						
		try {
			String xmlPath = getDirectoryPath(reponame, req);
			String svgPath = xmlPath.replace(".kite9.xml", ".kite9.svg");
			GitHub github = apiFactory.createApiFor(authentication);
			GHPerson p = getUserOrOrg(type, userorg, github);
			GHRepository repo = p.getRepository(reponame);
			String fullUrl = req.getRequestURL().toString();
			String branchName = repo.getDefaultBranch();
			GHBranch branch = repo.getBranch(branchName);
			GHTree tree = repo.getTree(branchName);
			
			ADL input = getKite9File(authentication, type, userorg, reponame, xmlPath, headers, fullUrl);

			if (log.go()) {
				log.send("Before: " + input.getAsADLString());
			}
			
			input = (ADL) performSteps(request, input, headers, new URI(fullUrl));
			checkRenderable(input);
			
			// submit the blobs
			String adl = input.getAsADLString();
			String svg = new XMLHelper().toXML(input.getAsSVGRepresentation());

			Date d = new Date();
			
			GHBlob adlBlob = repo.createBlob().textContent(adl).create();
			GHBlob svgBlob = repo.createBlob().textContent(svg).create();

			// now create a directory tree with these files in it
			GHTree newTree = repo.createTree()
				.baseTree(tree.getSha())
				.add(xmlPath, adl, false)
				.add(svgPath, svg, false)
				.create();
			
			GHCommit c = repo.createCommit()
				.committer(GitHubAPIFactory.getUserLogin(authentication), GitHubAPIFactory.getEmail(authentication), d)
				.message("Kite9 Diagram Change")
				.parent(branch.getSHA1())
				.tree(newTree.getSha())
				.create();
			
			repo.getRef("heads/"+branchName).updateTo(c.getSHA1());
			
			if (log.go()) {
				log.send("After: " + input.getAsADLString());
			}
			
			return input;
		} catch (CommandException e) {
			throw e;
		} catch (Throwable e) {
			throw new CommandException(HttpStatus.CONFLICT, "Couldn't process commands", e, request);
		} 
	}
	




	private boolean needsRevision(List<Command> body) {
		for (Command command : body) {
			if (command instanceof XMLCommand) {
				return true;
			}
		}
		
		return false;
	}





	@Override
	public String getPrefix() {
		return "RCC ";
	}

	protected void checkRenderable(ADL input) {
		input.getAsSVGRepresentation();
	}
}
