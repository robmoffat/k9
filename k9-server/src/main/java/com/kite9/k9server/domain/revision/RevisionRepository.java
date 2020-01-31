package com.kite9.k9server.domain.revision;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Component;

import com.kite9.k9server.domain.entity.RestEntityCrudRepository;

@Component
@RepositoryRestResource(excerptProjection=RevisionExcerptProjection.class)
public interface RevisionRepository extends RestEntityCrudRepository<Revision> {

	@Override
	@Query("select r from Revision r join r.document.project.members m where m.user.email = ?#{ principal }")
	public Iterable<Revision> findAll();
	
	@Override
	@Query( "select r from Revision r join r.document.project.members m where r.id in :ids and m.user.email = ?#{ principal }")
	public Iterable<Revision> findAllById(Iterable<Long> ids);
	
}
