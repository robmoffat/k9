package com.kite9.k9server.command.controllers;

import java.net.URI;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;

import org.kite9.framework.common.Kite9ProcessingException;
import org.kite9.framework.logging.Logable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.core.mapping.ResourceMappings;
import org.springframework.data.rest.webmvc.BasePathAwareController;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.server.ResponseStatusException;

import com.kite9.k9server.adl.format.media.Kite9MediaTypes;
import com.kite9.k9server.adl.holder.ADL;
import com.kite9.k9server.adl.holder.ADLImpl;
import com.kite9.k9server.command.Command;
import com.kite9.k9server.command.CommandException;
import com.kite9.k9server.command.XMLCommand;
import com.kite9.k9server.domain.document.Document;
import com.kite9.k9server.domain.document.DocumentRepository;
import com.kite9.k9server.domain.links.ContentResourceProcessor;
import com.kite9.k9server.domain.revision.Revision;
import com.kite9.k9server.domain.revision.RevisionRepository;
import com.kite9.k9server.domain.user.User;
import com.kite9.k9server.domain.user.UserRepository;

/**
 * Accepts commands to the system in order to modify XML.  Contents are returned back in whatever format is
 * requested.  For /content POST.
 * 
 * @author robmoffat
 *
 */
@BasePathAwareController 
public class ContentCommandController extends AbstractCommandController implements Logable {
	
	@Autowired
	UserRepository userRepository;
	
	@Autowired
	RevisionRepository revisionRepository;
	
	@Autowired
	DocumentRepository documentRepository;
		
	@Autowired
	ResourceMappings mappings;
	
	/**
	 * This is used for applying commands to domain objects.
	 */
	@RequestMapping(method={RequestMethod.POST}, 
		path= {"/documents/{id}"+ContentResourceProcessor.CONTENT_URL}, 
		consumes= {MediaType.APPLICATION_JSON_VALUE},
		produces= {
			MediaTypes.HAL_JSON_VALUE, 
			Kite9MediaTypes.ADL_SVG_VALUE, 
			Kite9MediaTypes.SVG_VALUE
		}) 
	@Transactional
	public @ResponseBody ADL applyCommandOnResource (
				RequestEntity<List<Command>> req,
				@PathVariable(required=false, name="id") Long id) throws CommandException {
		
		Document ri = documentRepository.findById(id)
				.orElseThrow(() -> new CommandException(HttpStatus.NOT_FOUND,"No document found "+id, req.getBody()));
				
		try {

			ADL input = ADLImpl.xmlMode(req.getUrl(), ri.getCurrentRevision().getXml(), req.getHeaders());

			if (log.go()) {
				log.send("Before: " + input.getAsADLString());
			}
			
			input = (ADL) performSteps(req.getBody(), input, ri, req.getHeaders(), req.getUrl());
			
			createNewRevisionOnDocument(input, ri, needsRevision(req.getBody()));
			checkRenderable(input);
			
			if (log.go()) {
				log.send("After: " + input.getAsADLString());
			}
			
			addDocumentMeta(input, ri);
			return input;
		} catch (CommandException e) {
			throw e;
		} catch (Throwable e) {
			throw new CommandException(HttpStatus.CONFLICT, "Couldn't process commands", e, req.getBody());
		} 
	}
	
	@GetMapping(path="/{repository}/{id}"+ContentResourceProcessor.CONTENT_URL)
	public @ResponseBody ADL content(
			@PathVariable("repository") String repository,
			@PathVariable("id") long id,
			HttpServletRequest request,
			@RequestHeader HttpHeaders headers) throws Exception {
		
		String uri = request.getRequestURI();
		String url = request.getRequestURL().toString();
		
		if (repository.equals("documents")) {
			Document d = documentRepository.findById(id)
					.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No document "+id));
			String xml = d.getCurrentRevision().getXml();
					
			ADL out = ADLImpl.xmlMode(new URI(url), xml, headers);
			addDocumentMeta(out, d);
			return out;
		} else if (repository.equals("revisions")) {
			Revision r = revisionRepository.findById(id)
					.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No revision "+id));
			String xml = r.getXml();
			
			ADL out = ADLImpl.xmlMode(new URI(url), xml, headers);
			return out;
		} else {
			throw new Kite9ProcessingException("/content not available at "+uri);
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

	protected void createNewRevisionOnDocument(ADL input, Document d, boolean needsRevision) {
		if (needsRevision) {
			String username = SecurityContextHolder.getContext().getAuthentication().getName();
			User u = userRepository.findByUsername(username);
		
			Revision rOld = d.getCurrentRevision();
	
			// save the new revision
			Revision rNew = new Revision();
			rNew.setAuthor(u);
			rNew.setDocument(d);
			rNew.setXml(input.getAsADLString());
			rNew.setPreviousRevision(rOld);
			revisionRepository.save(rNew);
			
			// update the old revision
			rOld.setNextRevision(rNew);
			revisionRepository.save(rOld);
			
			// update the document
			d.setCurrentRevision(rNew);
		
			documentRepository.save(d);
		}
	}


	/**
	 * Since we are in a document, add some meta-data about revisions, and the redo situation.
	 */
	private ADL addDocumentMeta(ADL adl, Document d) {
		Revision r = d.getCurrentRevision();
		adl.setMeta("redo", ""+(r.getNextRevision() != null));
		adl.setMeta("undo", ""+(r.getPreviousRevision() != null));
		adl.setMeta("author", r.getAuthor().getUsername());
		
		String revisionUrl = entityLinks.linkFor(Revision.class).slash(r.getId()).toString();
		String documentUrl = entityLinks.linkFor(Document.class).slash(r.getDocument().getId()).toString();
		
		adl.setMeta("revision", revisionUrl+ContentResourceProcessor.CONTENT_URL);
		adl.setMeta(Link.REL_SELF, documentUrl);
		adl.setMeta(ContentResourceProcessor.CONTENT_REL, documentUrl+ContentResourceProcessor.CONTENT_URL);
		return adl;
	}

	@Override
	public String getPrefix() {
		return "RCC ";
	}

	protected void checkRenderable(ADL input) {
		input.getAsSVGRepresentation();
	}
}
