package com.kite9.k9server.domain.entity;

import java.util.Optional;

import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;

/**
 * Minimises the surface area of methods we need to secure to just save() and delete().
 * Handles pre/post authorize checks to make sure that the objects are checked correctly.
 */
@NoRepositoryBean
public interface SecuredCrudRepository<T extends RestEntity> extends RestEntityRepository<T> {
	
	static final String THIS_CLASS_NAME = SecuredCrudRepository.class.getCanonicalName();
	
	@PreAuthorize("#entity == null ? true : #entity.checkWrite()")
	@RestResource(exported=false)
	public T save(T r);

	@PreAuthorize("#entity == null ? true : #entity.checkDelete()")
	@RestResource(exported=false)
	public void delete(@Param("entity") T entity);

	@PostAuthorize("returnObject.isPresent() ? returnObject.get().checkRead() : true")
	@Override
	public <S extends T> Optional<S> findById(@Param("id") Long id);
}