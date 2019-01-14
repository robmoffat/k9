package com.kite9.k9server.domain.revision;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.PersistentEntityResource;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.ResourceProcessor;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Allows you to pull back the content of the revision
 * 
 * @author robmoffat
 */
@Controller
@RequestMapping(path="/api/revisions")
public class RevisionController implements ResourceProcessor<PersistentEntityResource> {

	public static final String CONTENT_REL = "content";
	public static final String CONTENT_URL = "/content";
	
	@Autowired
	RevisionRepository repo;
	
	/**
	 * Returns the current revision.
	 */
	@RequestMapping(path = "/{revisionId}"+CONTENT_URL, method= {RequestMethod.GET}) 
	public @ResponseBody Revision input(@PathVariable("revisionId") long id) {
		Revision r = repo.findOne(id);
		return r;
	}
	
	public static String createRevisionControllerUrl(Long id) {
		String url = ControllerLinkBuilder.linkTo(RevisionController.class).toString();
		return url + "/"+id;
	}
	
	@Override
	public PersistentEntityResource process(PersistentEntityResource resource) {
		if (resource.getContent() instanceof Revision) {
			Revision r = (Revision) resource.getContent();
			resource.add(new Link(createRevisionControllerUrl(r.getId()) + CONTENT_URL, CONTENT_REL));
		}
		
		return resource;
	}

}