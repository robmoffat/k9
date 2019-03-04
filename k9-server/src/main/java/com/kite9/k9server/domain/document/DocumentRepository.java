package com.kite9.k9server.domain.document;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

@Component
public interface DocumentRepository extends CrudRepository<Document, Long> {

	@Override
	@PreAuthorize("#entity.checkWrite()")
	public Document save(@Param("entity") Document document);

	@RestResource(exported=false)
	public <S extends Document> Iterable<S> saveAll(Iterable<S> entities);

	@Override
//	@PostAuthorize("returnObject.project.checkAccess(false)")
	public Optional<Document> findById(Long id);

	@RestResource(exported=false)
	public boolean existsById(Long id);

	@Override
//	@Query("select d from Document d join d.project.members m where m.user.username = ?#{ principal }")
	public Iterable<Document> findAll();
	
//	@Query( "select d from Document d where d.id in :ids" )
	public Iterable<Document> findAllById(Iterable<Long> ids);

	@RestResource(exported=false)
	public long count();

	@Override
	public void deleteById(Long id);

//	@PreAuthorize("#entity.project.checkAccess(true)")
	public void delete(Document entity);

	@RestResource(exported=false)
	public void deleteAll(Iterable<? extends Document> entities);

	@RestResource(exported=false)
	public void deleteAll();

	
	
}
