package com.kite9.k9server.domain;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.webmvc.PersistentEntityResource;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.ResourceProcessor;

public abstract class AbstractADLContentController<X extends AbstractLongIdEntity> implements ResourceProcessor<PersistentEntityResource> {

	public static final String CONTENT_REL = "content";
	public static final String CONTENT_URL = "/content";
	
	@Autowired
	protected CrudRepository<X, Long> repo;
	
	public abstract String createContentControllerUrl(Long id);
	
	@Override
	public PersistentEntityResource process(PersistentEntityResource resource) {
		if (appliesTo(resource.getContent())) {
			AbstractLongIdEntity r = (AbstractLongIdEntity) resource.getContent();
			resource.add(new Link(createContentControllerUrl(r.getId()) + CONTENT_URL, CONTENT_REL));
		}
		
		return resource;
	}

	protected abstract boolean appliesTo(Object content);
}
