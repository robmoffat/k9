package com.kite9.k9server.domain.project;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Component;

import com.kite9.k9server.domain.entity.SecuredCrudRepository;

@Component
@RepositoryRestResource(excerptProjection=ProjectExcerptProjection.class)
public interface ProjectRepository extends SecuredCrudRepository<Project> {

	@Override
	@Query("select p from Project p join p.members m where m.user.username = ?#{ principal }")
	public Iterable<Project> findAll();
	
	@Override
	@Query( "select p from Project p join p.members m where p.id in :ids and m.user.username = ?#{ principal }")
	public Iterable<Project> findAllById(Iterable<Long> ids);
	
}
