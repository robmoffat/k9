package com.kite9.k9server.domain;

import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.security.access.prepost.PreAuthorize;

/**
 * Minimises the surface area of methods we need to secure.  
 */
@NoRepositoryBean
public interface SecuredCrudRepository<T, ID> extends Repository<T, ID> {

	@PreAuthorize("#entity.checkWrite()")
	public <S extends T> S save(@Param("entity") S entity);

	@RestResource(exported=false)
	public <S extends T> Iterable<S> saveAll(Iterable<S> entities);

	@RestResource(exported=false)
	public boolean existsById(Long id);

	@RestResource(exported=false)
	public long count();

	@RestResource(exported=false)
	public void deleteById(ID id);

	@PreAuthorize("#entity.checkDelete()")
	public void delete(@Param("entity") T entity);

	@RestResource(exported=false)
	public void deleteAll(Iterable<? extends T> entities);

	@RestResource(exported=false)
	public void deleteAll();

}
