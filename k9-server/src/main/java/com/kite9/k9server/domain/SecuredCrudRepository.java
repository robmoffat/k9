package com.kite9.k9server.domain;

import java.util.Optional;

import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;

/**
 * Minimises the surface area of methods we need to secure.  Save is left for the individual
 * repositories to define, as are the findAll and findAllById.
 */
@NoRepositoryBean
public interface SecuredCrudRepository<T extends RestEntity> extends RestEntityRepository<T> {
	
	static final String THIS_CLASS_NAME = SecuredCrudRepository.class.getCanonicalName();

	@RestResource(exported=false)
	public <S extends T> Iterable<S> saveAll(Iterable<S> entities);
	
	@RestResource(exported=false)
	public T save(T r);

	@RestResource(exported=false)
	public boolean existsById(Long id);

	@RestResource(exported=false)
	public long count();

	@RestResource(exported=false)
	public void deleteById(Long id);

	@RestResource(exported=false)
	@PreAuthorize("#entity == null ? true : #entity.checkDelete()")
	public void delete(@Param("entity") T entity);

	@RestResource(exported=false)
	public void deleteAll(Iterable<? extends T> entities);

	@RestResource(exported=false)
	public void deleteAll();
	
	@PostAuthorize("returnObject.isPresent() ? returnObject.get().checkRead() : true")
	@Override
	public <S extends T> Optional<S> findById(@Param("id") Long id);
}
