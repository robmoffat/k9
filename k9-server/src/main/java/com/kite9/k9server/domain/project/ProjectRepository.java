package com.kite9.k9server.domain.project;

import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Component;

import com.kite9.k9server.domain.SecuredCrudRepository;

@Component
public interface ProjectRepository extends SecuredCrudRepository<Project, Long>, ProjectRepositoryCustom {

	@Query( "select p from Project p join p.members m where p.id = :id and m.user.username = ?#{ principal }" )
	public Optional<Project> findById(@Param("id") Long id);

	@Query("select p from Project p join p.members m where m.user.username = ?#{ principal }")
	public Iterable<Project> findAll();
	
	@Query( "select p from Project p join p.members m where p.id in :ids and m.user.username = ?#{ principal }")
	public Iterable<Project> findAllById(Iterable<Long> ids);
	
}
