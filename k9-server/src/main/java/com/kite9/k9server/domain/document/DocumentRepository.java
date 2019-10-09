package com.kite9.k9server.domain.document;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Component;

import com.kite9.k9server.domain.SecuredCrudRepository;

@Component
@RepositoryRestResource(excerptProjection=DocumentExcerptProjection.class)
public interface DocumentRepository extends SecuredCrudRepository<Document>, DocumentRepositoryCustom {

	@Query("select d from Document d join d.project.members m where m.user.username = ?#{ principal }")
	public Iterable<Document> findAll();
	
	@Query( "select d from Document d join d.project.members m where d.id in :ids and m.user.username = ?#{ principal }")
	public Iterable<Document> findAllById(Iterable<Long> ids);
	
}
