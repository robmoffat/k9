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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.kite9.k9server.adl.holder.ADL;
import com.kite9.k9server.adl.holder.ADLImpl;
import com.kite9.k9server.command.Command;
import com.kite9.k9server.command.CommandController;
import com.kite9.k9server.domain.AbstractADLContentController;
import com.kite9.k9server.domain.AbstractLongIdEntity;
import com.kite9.k9server.domain.revision.Revision;
import com.kite9.k9server.domain.revision.RevisionRepository;
import com.kite9.k9server.domain.user.User;

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
	
	/**
	 * Returns the current revision contents.
	 */
	@RequestMapping(path = "/{documentId}"+CONTENT_URL, method= {RequestMethod.GET}) 
	public @ResponseBody ADL input(@PathVariable("documentId") long id, HttpServletRequest request) {
		Revision r = getCurrentRevision(id);
		return buildADL(request, r);
	}

	private Revision getCurrentRevision(long docId) {
		Optional<Document> or = repo.findById(docId);
		Document d = or.orElseThrow(() ->  new ResourceNotFoundException("No document for "+docId));
		Revision r = d.getCurrentRevision();
		
		if (r == null) {
			throw new ResourceNotFoundException("No revisions for document "+docId);
		}
		return r;
	}
	
	/**
	 * Applies a command to the current revision, using the {@link CommandController}.
	 */
	@Transactional
	@RequestMapping(path = "/{documentId}"+CHANGE_URL, method = RequestMethod.POST) 
	public @ResponseBody ADL change(
			@PathVariable("documentId") long id, 
			HttpServletRequest request, 
			@RequestBody List<Command> steps,
			@AuthenticationPrincipal User user) {
		Revision rOld = getCurrentRevision(id);
		Document d = rOld.getDocument();
		ADL adl = buildADL(request, rOld);
		ADL out = command.applyCommand(steps, adl);
		
		// create the new revision
		Revision rNew = new Revision();
		rNew.setAuthor(user);
		rNew.setDocument(d);
		rNew.setPreviousRevision(rOld);
		rNew.setXml(out.getAsXMLString());
		
		d.setCurrentRevision(rNew);
		
		revisions.save(rNew);
		rOld.setNextRevision(rNew);
		revisions.save(rOld);
		
		return out;
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
		resource.add(new Link(createContentControllerUrl(r.getId()) + CHANGE_URL, CHANGE_REL));
	}

	
}
