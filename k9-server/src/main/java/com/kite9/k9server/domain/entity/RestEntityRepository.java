package com.kite9.k9server.domain.entity;

import java.util.Optional;

import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.Repository;

/**
 * Base class for all the repositories returning our rest entities.
 * 
 * @author robmoffat
 *
 * @param <X>
 */
@NoRepositoryBean
public interface RestEntityRepository<X extends RestEntity> extends Repository<X, Long> {

	public <S extends X> Optional<S> findById(Long id);
	
	public Iterable<X> findAll();
	
	public Iterable<X> findAllById(Iterable<Long> ids);
}
