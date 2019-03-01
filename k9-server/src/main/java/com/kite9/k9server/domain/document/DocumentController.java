package com.kite9.k9server.domain.document;

import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.kite9.k9server.adl.holder.ADL;
import com.kite9.k9server.adl.holder.ADLImpl;
import com.kite9.k9server.domain.AbstractADLContentController;
import com.kite9.k9server.domain.revision.Revision;

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
	
	/**
	 * Returns the current revision contents.
	 */
	@RequestMapping(path = "/{documentId}"+CONTENT_URL, method= {RequestMethod.GET}) 
	public @ResponseBody ADL input(@PathVariable("documentId") long id, HttpServletRequest request) {
		Optional<Document> or = repo.findById(id);
		Document d = or.orElseThrow(() ->  new ResourceNotFoundException("No document for "+id));
		Revision r = d.getCurrentRevision();
		
		if (r == null) {
			throw new ResourceNotFoundException("No revisions for document "+id);
		}
		
		String url = request.getRequestURL().toString();
		return new ADLImpl(r.getXml(), url);
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

	
}
