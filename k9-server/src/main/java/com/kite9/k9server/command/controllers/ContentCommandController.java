package com.kite9.k9server.command.controllers;

import java.net.URI;
import java.util.List;

import org.kite9.framework.logging.Logable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.core.mapping.ResourceMappings;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.server.ResponseStatusException;

import com.kite9.k9server.adl.format.media.Kite9MediaTypes;
import com.kite9.k9server.adl.holder.ADL;
import com.kite9.k9server.adl.holder.ADLImpl;
import com.kite9.k9server.command.Command;
import com.kite9.k9server.command.CommandException;
import com.kite9.k9server.command.domain.DomainCommand;
import com.kite9.k9server.command.xml.XMLCommand;
import com.kite9.k9server.domain.document.Document;
import com.kite9.k9server.domain.document.DocumentRepository;
import com.kite9.k9server.domain.entity.RestEntity;
import com.kite9.k9server.domain.entity.Secured;
import com.kite9.k9server.domain.rels.ContentResourceProcessor;
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
@RepositoryRestController 
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
		path= {"/documents/{id}/content"}, 
		consumes= {MediaType.APPLICATION_JSON_VALUE},
		produces= {
			MediaTypes.HAL_JSON_VALUE, 
			Kite9MediaTypes.ADL_SVG_VALUE, 
			Kite9MediaTypes.SVG_VALUE
		}) 
	@ResponseBody
	public Object applyCommandOnResource (
				RequestEntity<List<Command>> req,
				@PathVariable(required=false, name="id") Long id) throws CommandException {
		
		Document ri = documentRepository.findById(id)
				.orElseThrow(() -> new CommandException("No document found "+id, req.getBody()));
		
		if (!ri.checkWrite()) {
			 throw new CommandException("Couldn't write "+id, req.getBody());
		}
				
		try {
			ADL out = withinTransaction(req.getBody(), ri, req.getHeaders(), req.getUrl());
			addDocumentMeta(out, ri);
			return out;
		} catch (CommandException e) {
			throw e;
		} catch (Throwable e) {
			throw new CommandException("Couldn't process commands", e, req.getBody());
		} 
	}

	@Transactional
	protected ADL withinTransaction(List<Command> c, RestEntity ri, HttpHeaders headers, URI uri) {
		Class<? extends Command> type = identifyCommandTypes(c);
		if ((type == XMLCommand.class) && (ri instanceof Document)) {
			ADL input = new ADLImpl(((Document) ri).getCurrentRevision().getXml(), uri, headers);
			return performXMLCommands(c, input, ri, headers, uri);
		} 
		
		throw new CommandException("Couldn't process command steps", c);
	}

	protected Object performDomainCommands(List<Command> steps, RestEntity context, HttpHeaders headers, URI url) {
		checkDomainAccess(context, url);
		return performSteps(steps, null, context, headers, url);
	}

	protected ADL performXMLCommands(List<Command> steps, ADL input, RestEntity context, HttpHeaders headers, URI url) {
		Document d = null;
		
		d = checkDocumentAccess(context, url, d);
		input = super.performXMLCommands(steps, input, context, headers, url);
		
		if (d != null) {
			// create the new revision
			createNewRevisionOnDocument(input, d);
		}	
		
		return input;
	}

	protected Revision createNewRevisionOnDocument(ADL input, Document d) {
		String username = SecurityContextHolder.getContext().getAuthentication().getName();
		User u = userRepository.findByUsername(username);
		Revision rOld = d.getCurrentRevision();

		// save the new revision
		Revision rNew = new Revision();
		rNew.setAuthor(u);
		rNew.setDocument(d);
		rNew.setXml(input.getAsXMLString());
		rNew.setPreviousRevision(rOld);
		revisionRepository.save(rNew);
		
		// update the old revision
		rOld.setNextRevision(rNew);
		revisionRepository.save(rOld);
		
		// update the document
		d.setCurrentRevision(rNew);
		documentRepository.save(d);
		
		return rNew;
	}


	/**
	 * Since we are in a document, add some meta-data about revisions, and the redo situation.
	 */
	private ADL addDocumentMeta(ADL adl, Document d) {
		Revision r = d.getCurrentRevision();
		adl.setMeta("redo", ""+(r.getNextRevision() != null));
		adl.setMeta("undo", ""+(r.getPreviousRevision() != null));
		adl.setMeta("revision", entityLinks.linkFor(Revision.class).slash(r.getId()).toString());
		String documentUrl = entityLinks.linkFor(Document.class).slash(r.getDocument().getId()).toString();
		adl.setMeta(Link.REL_SELF, documentUrl);
		adl.setMeta(ContentResourceProcessor.CONTENT_REL, documentUrl+ContentResourceProcessor.CONTENT_URL);
		adl.setMeta("author", r.getAuthor().getUsername());
		return adl;
	}

	protected Document checkDocumentAccess(RestEntity context, URI url, Document d) {
		if (context != null) {
			if (!(context instanceof Document)) {
				throw new CommandException("XMLCommands must apply to a document "+url);
			}

			d = (Document) context;
			checkDomainAccess(d, url);
		}
		return d;
	}
	
	protected void checkDomainAccess(RestEntity d, URI url) {
		if (d instanceof Secured) {
			if (!((Secured) d).checkWrite()) {
				throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "No write access for "+url);
			}
		}
	}


	private Class<? extends Command> identifyCommandTypes(List<Command> steps) {
		Class<? extends Command> out = null;
		for (Command command : steps) {
			if (out == null) {
				if (command instanceof XMLCommand) {
					out = XMLCommand.class;
				} else if (command instanceof DomainCommand) {
					out = DomainCommand.class;
				} else {
					throw new CommandException("Can't identity type of command", command);
				}
			} else {
				if (!out.isAssignableFrom(command.getClass())) {
					throw new CommandException("Command "+command+" should also be an instance of "+out.getName(), command);
				}
			}
		}
		
		return out;
	}

	@Override
	public String getPrefix() {
		return "RCC ";
	}
}
