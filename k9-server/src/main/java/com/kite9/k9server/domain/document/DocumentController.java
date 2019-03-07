package com.kite9.k9server.domain.document;

import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.PersistentEntityResource;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.kite9.k9server.adl.holder.ADL;
import com.kite9.k9server.command.Command;
import com.kite9.k9server.command.CommandController;
import com.kite9.k9server.command.CommandException;
import com.kite9.k9server.domain.AbstractADLContentController;
import com.kite9.k9server.domain.AbstractLongIdEntity;
import com.kite9.k9server.domain.revision.Revision;
import com.kite9.k9server.domain.revision.RevisionRepository;
import com.kite9.k9server.domain.user.User;
import com.kite9.k9server.domain.user.UserRepository;

/**
 * Allows you to pull back the content of the latest revision from the /content url.
 * Also allows you to create a new revision by applying a command.
 * 
 * @author robmoffat
 */
@Controller
@RequestMapping(path="/api/documents")
public class DocumentController extends AbstractADLContentController<Document> {

	public static final String CHANGE_REL = "change";
	public static final String CHANGE_URL = "/change";
	
	@Autowired
	CommandController command;
	
	@Autowired
	RevisionRepository revisions;
	
	@Autowired
	UserRepository userRepository;
	
	/**
	 * Returns the current revision contents.
	 */
	@RequestMapping(path = "/{documentId}"+CONTENT_URL, method= {RequestMethod.GET}) 
	public @ResponseBody ADL input(@PathVariable("documentId") long id, HttpServletRequest request) {
		Revision r = getCurrentRevision(id);
		return buildADL(request, r);
	}
	
	/**
	 * Applies a command to the current revision, using the {@link CommandController}.
	 */
	@Transactional
	@RequestMapping(path = "/{documentId}"+CHANGE_URL, method = RequestMethod.POST) 
	public @ResponseBody ADL change(
			@PathVariable("documentId") long id, 
			HttpServletRequest request, 
			@RequestBody List<Command> steps) {
		Revision rOld = getCurrentRevision(id);
		String username = SecurityContextHolder.getContext().getAuthentication().getName();
		User u = userRepository.findByUsername(username);
		
		Document d = rOld.getDocument();
		ADL adl = buildADL(request, rOld);
		
		if (isUndoRedo(request, steps, rOld)) {
			ADL out = command.applyCommand(steps, adl);
			return out;
		} else {
			// creates a new revision. Integrity and document changes are handled by 
			// revisionRepository.

			ADL out = command.applyCommand(steps, adl);
			Revision rNew = new Revision();
			rNew.setAuthor(u);
			rNew.setDocument(d);
			rNew.setXml(out.getAsXMLString());
			revisions.save(rNew);
			
			return out;
		}
	}
	
	private boolean isUndoRedo(HttpServletRequest request, List<Command> steps, Revision r) {
		boolean contextSet = false;
		
		for (Command command : steps) {
			if (command instanceof HasDocument) {
				((HasDocument)command).setCommandContext((DocumentRepository) repo, r, request.getRequestURL().toString());
				contextSet = true;
			}
		}
		
		if (contextSet && (steps.size() > 1)) {
			throw new CommandException("Undo/Redo commands must be a supplied alone rather than as multiple steps", null);
		}
		
		return contextSet;
	}

	protected Revision getCurrentRevision(long docId) {
		Optional<Document> or = repo.findById(docId);
		Document d = or.orElseThrow(() ->  new ResourceNotFoundException("No document for "+docId));
		Revision r = d.getCurrentRevision();
		
		if (r == null) {
			throw new ResourceNotFoundException("No revisions for document "+docId);
		}
		return r;
	}

	@Override
	public String createContentControllerUrl(Long id) {
		String url = ControllerLinkBuilder.linkTo(DocumentController.class).toString();
		return url + "/"+id;
	}

	@Override
	protected boolean appliesTo(Object content) {
		return content instanceof Document;
	}

	@Override
	protected void addRels(PersistentEntityResource resource, AbstractLongIdEntity r) {
		super.addRels(resource, r);
		String cUrl = createContentControllerUrl(r.getId());
		resource.add(new Link(cUrl + CHANGE_URL, CHANGE_REL));
	}

	
}