package com.kite9.k9server.domain.document;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

import com.kite9.k9server.domain.SecuredCrudRepository;

@Component
public interface DocumentRepository extends SecuredCrudRepository<Document, Long> {

	/**
	 * Handles save, ensures that the creator has privileges.
	 */
	@PreAuthorize("#entity.checkWrite()")
	public Document save(@Param("entity") Document r);

	@Query("select d from Document d join d.project.members m where m.user.username = ?#{ principal }")
	public Iterable<Document> findAll();
	
	@Query( "select d from Document d join d.project.members m where d.id in :ids and m.user.username = ?#{ principal }")
	public Iterable<Document> findAllById(Iterable<Long> ids);
	
}
