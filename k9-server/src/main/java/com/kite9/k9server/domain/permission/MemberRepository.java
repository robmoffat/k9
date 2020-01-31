package com.kite9.k9server.domain.permission;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Component;

import com.kite9.k9server.domain.entity.RestEntityCrudRepository;

@Component
@RepositoryRestResource(excerptProjection=MemberExcerptProjection.class)
public interface MemberRepository extends RestEntityCrudRepository<Member> {

	@Override
	@Query("select m from Member m where m.user.email = ?#{ principal }")
	public Iterable<Member> findAll();
	
	@Override
	@Query( "select m from Member m where m.id in :ids and m.user.email = ?#{ principal }")
	public Iterable<Member> findAllById(Iterable<Long> ids);
	
}
