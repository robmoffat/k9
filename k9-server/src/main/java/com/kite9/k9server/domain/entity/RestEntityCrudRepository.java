package com.kite9.k9server.domain.entity;

import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RestResource;

/**
 * Provides save/delete methods for RestEntity's.
 */
@NoRepositoryBean
public interface RestEntityCrudRepository<T extends RestEntity> extends RestEntityRepository<T> {
	
//	static final String THIS_CLASS_NAME = RestEntityCrudRepository.class.getCanonicalName();
	
	@RestResource(exported=false)
	public T save(@Param("entity") T r);

	@RestResource(exported=false)
	public void delete(@Param("entity") T entity);

}