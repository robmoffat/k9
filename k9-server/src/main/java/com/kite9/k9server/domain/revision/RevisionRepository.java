package com.kite9.k9server.domain.revision;

import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Component;

import com.kite9.k9server.domain.SecuredCrudRepository;

@Component
@RepositoryRestResource(excerptProjection=DefaultExcerptProjection.class)
public interface RevisionRepository extends SecuredCrudRepository<Revision, Long>, RevisionRepositoryCustom {

	@Query( "select r from Revision r join r.document.project.members m where r.id = :id and m.user.username = ?#{ principal }" )
	public Optional<Revision> findById(@Param("id") Long id);

	@Query("select r from Revision r join r.document.project.members m where m.user.username = ?#{ principal }")
	public Iterable<Revision> findAll();
	
	@Query( "select r from Revision r join r.document.project.members m where r.id in :ids and m.user.username = ?#{ principal }")
	public Iterable<Revision> findAllById(Iterable<Long> ids);
	
}
