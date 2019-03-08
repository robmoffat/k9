package com.kite9.k9server.domain.revision;

import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.kite9.k9server.adl.holder.ADL;
import com.kite9.k9server.domain.AbstractADLContentController;
import com.kite9.k9server.web.HttpException;

/**
 * Allows you to pull back the content of the revision from the /content url.
 * 
 * @author robmoffat
 */
@Controller
@RequestMapping(path="/api/revisions")
public class RevisionController extends AbstractADLContentController<Revision> {

	/**
	 * Returns the current revision ADL.
	 */
	@RequestMapping(path = "/{revisionId}"+CONTENT_URL, method= {RequestMethod.GET}) 
	public @ResponseBody ADL input(@PathVariable("revisionId") long id, HttpServletRequest request) {
		Optional<Revision> or = ((RevisionRepository) repo).findById(id);
		Revision r = or.orElseThrow(() ->  new HttpException(HttpStatus.NOT_FOUND, "No revision for "+id));
		return buildADL(request, r);
	}

	@Override
	public String createContentControllerUrl(Long id) {
		String url = ControllerLinkBuilder.linkTo(RevisionController.class).toString();
		return url + "/"+id;
	}

	@Override
	protected boolean appliesTo(Object content) {
		return content instanceof Revision;
	}
	
}
