package com.kite9.k9server.domain.links;

import java.net.URI;

import org.springframework.data.rest.webmvc.PersistentEntityResource;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.ResourceProcessor;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Component;

import com.kite9.k9server.adl.holder.ADL;
import com.kite9.k9server.adl.holder.ADLImpl;
import com.kite9.k9server.domain.document.Document;
import com.kite9.k9server.domain.entity.AbstractLongIdEntity;
import com.kite9.k9server.domain.revision.Revision;

/**
 * Adds url/rel to the rest object so we can get /content from it.  Relevant to 
 * @author robmoffat
 *
 * @param <X>
 */
@Component
public class ContentResourceProcessor implements ResourceProcessor<PersistentEntityResource> {

	public static final String CONTENT_REL = "content";
	public static final String CONTENT_URL = "/content";
	
	@Override
	public PersistentEntityResource process(PersistentEntityResource resource) {
		if (appliesTo(resource.getContent())) {
			AbstractLongIdEntity r = (AbstractLongIdEntity) resource.getContent();
			addContentRels(resource, r);
		}
		
		return resource;
	}

	protected void addContentRels(PersistentEntityResource resource, AbstractLongIdEntity r) {
		resource.add(new Link(createContentControllerUrl(resource), CONTENT_REL));
	}
	
	private String createContentControllerUrl(PersistentEntityResource resource) {
		Link l = resource.getLink(Link.REL_SELF);
		return l.getHref()+CONTENT_URL;
	}

	public ADL buildADL(RequestEntity<?> request, Revision r) {
		URI url =request.getUrl();
		return ADLImpl.xmlMode(url, r.getXml(), request.getHeaders());
	}

	protected boolean appliesTo(Object content) {
		return (content instanceof Document) ||(content instanceof Revision);
	}
}
