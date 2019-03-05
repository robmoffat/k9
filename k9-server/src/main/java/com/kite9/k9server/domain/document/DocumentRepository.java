package com.kite9.k9server.domain.document;

import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;

import com.kite9.k9server.domain.SecuredCrudRepository;

@Component
public interface DocumentRepository extends SecuredCrudRepository<Document, Long> {

	@Query( "select d from Document d join d.project.members m where d.id = :id and m.user.username = ?#{ principal }" )
	public Optional<Document> findById(@Param("id") Long id);

	@Query("select d from Document d join d.project.members m where m.user.username = ?#{ principal }")
	public Iterable<Document> findAll();
	
	@Query( "select d from Document d join d.project.members m where d.id in :ids and m.user.username = ?#{ principal }")
	public Iterable<Document> findAllById(Iterable<Long> ids);
	
}
