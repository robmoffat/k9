package com.kite9.k9server.domain.project;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Component;

import com.kite9.k9server.domain.entity.RestEntityCrudRepository;

@Component
@RepositoryRestResource(excerptProjection=ProjectExcerptProjection.class)
public interface ProjectRepository extends RestEntityCrudRepository<Project> {

	@Override
	@Query("select p from Project p join p.members m where m.user.email = ?#{ principal }")
	public Iterable<Project> findAll();
	
	@Override
	@Query( "select p from Project p join p.members m where p.id in :ids and m.user.email = ?#{ principal }")
	public Iterable<Project> findAllById(Iterable<Long> ids);
	
}
