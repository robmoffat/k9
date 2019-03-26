package com.kite9.k9server.domain;

import java.net.URI;
import java.net.URISyntaxException;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.PersistentEntityResource;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.ResourceProcessor;

import com.kite9.k9server.adl.holder.ADL;
import com.kite9.k9server.adl.holder.ADLImpl;
import com.kite9.k9server.domain.revision.Revision;

public abstract class AbstractADLContentController<X extends AbstractLongIdEntity> implements ResourceProcessor<PersistentEntityResource> {

	public static final String CONTENT_REL = "content";
	public static final String CONTENT_URL = "/content";
	
	@Autowired
	protected SecuredCrudRepository<X, Long> repo;
	
	public abstract String createContentControllerUrl(Long id);
	
	@Override
	public PersistentEntityResource process(PersistentEntityResource resource) {
		if (appliesTo(resource.getContent())) {
			AbstractLongIdEntity r = (AbstractLongIdEntity) resource.getContent();
			addRels(resource, r);
		}
		
		return resource;
	}

	protected void addRels(PersistentEntityResource resource, AbstractLongIdEntity r) {
		resource.add(new Link(createContentControllerUrl(r.getId()) + CONTENT_URL, CONTENT_REL));
	}
	
	public ADL buildADL(HttpServletRequest request, Revision r) throws URISyntaxException {
		URI url = new URI(request.getRequestURI());
		return new ADLImpl(r.getXml(), url);
	}

	protected abstract boolean appliesTo(Object content);
}
