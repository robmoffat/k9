package com.kite9.k9server.domain.links;

import java.net.URI;
import java.util.Optional;

import org.springframework.data.rest.webmvc.PersistentEntityResource;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.RepresentationModelProcessor;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Component;

import com.kite9.k9server.adl.holder.ADL;
import com.kite9.k9server.adl.holder.ADLImpl;
import com.kite9.k9server.domain.entity.AbstractEntity;
import com.kite9.k9server.domain.project.Document;
import com.kite9.k9server.domain.revision.Revision;

/**
 * Adds url/rel to the rest object so we can get /content from it.  Relevant to 
 * @author robmoffat
 *
 * @param <X>
 */
//@Component
public class ContentResourceProcessor implements RepresentationModelProcessor<PersistentEntityResource> {

	public static final String CONTENT_REL = "content";
	public static final String CONTENT_URL = "/content";
	
	@Override
	public PersistentEntityResource process(PersistentEntityResource resource) {
		if (appliesTo(resource.getContent())) {
			AbstractEntity r = (AbstractEntity) resource.getContent();
			addContentRels(resource, r);
		}
		
		return resource;
	}

	protected void addContentRels(PersistentEntityResource resource, AbstractEntity r) {
		Optional<String> s = createContentControllerUrl(resource);
		s.ifPresent(ss -> resource.add(new Link(CONTENT_REL, ss)));
	}
	
	private Optional<String> createContentControllerUrl(PersistentEntityResource resource) {
		Optional<Link> ol = resource.getLink(IanaLinkRelations.SELF);
		return ol.map(l -> l.getHref()+CONTENT_URL);
	}

	public ADL buildADL(RequestEntity<?> request, Revision r) {
		URI url =request.getUrl();
		return ADLImpl.xmlMode(url, r.getXml(), request.getHeaders());
	}

	protected boolean appliesTo(Object content) {
		return (content instanceof Document) ||(content instanceof Revision);
	}
}
