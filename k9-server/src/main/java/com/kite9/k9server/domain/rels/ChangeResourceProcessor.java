package com.kite9.k9server.domain.rels;

import org.springframework.data.rest.webmvc.PersistentEntityResource;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.ResourceProcessor;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.springframework.stereotype.Component;

import com.kite9.k9server.domain.entity.AbstractLongIdEntity;
import com.kite9.k9server.domain.entity.Secured;

/**
 * Allows you to pull back the content of the latest revision from the /content url.
 * Also allows you to create a new revision by applying a command.
 * 
 * @author robmoffat
 */
//@Component
public class ChangeResourceProcessor implements ResourceProcessor<PersistentEntityResource> {

	public static final String CHANGE_REL = "change";
	public static final String CHANGE_URL = "/change";

	@Override
	public PersistentEntityResource process(PersistentEntityResource resource) {
		if (appliesTo(resource.getContent())) {
			AbstractLongIdEntity r = (AbstractLongIdEntity) resource.getContent();
			addChangeRels(resource, r);
		}
		
		return resource;
	}
	
	public String createContentControllerUrl(PersistentEntityResource resource) {
		Link l = resource.getLink(Link.REL_SELF);
		return l.getHref()+CHANGE_URL;
	}
	
	public static String documentUrl(Long id) {
		String url = ControllerLinkBuilder.linkTo(ChangeResourceProcessor.class).toString();
		return url + "/"+id;
	}

	protected boolean appliesTo(Object content) {
		return (content instanceof Secured) && (((Secured) content).checkWrite());
	}

	protected void addChangeRels(PersistentEntityResource resource, AbstractLongIdEntity r) {
		String cUrl = createContentControllerUrl(resource);
		resource.add(new Link(cUrl, CHANGE_REL));
	}
	
}
