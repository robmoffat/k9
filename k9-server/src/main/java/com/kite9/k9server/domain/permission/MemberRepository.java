package com.kite9.k9server.domain.permission;

import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Component;

import com.kite9.k9server.domain.SecuredCrudRepository;

@Component
@RepositoryRestResource(excerptProjection=DefaultExcerptProjection.class)
public interface MemberRepository extends SecuredCrudRepository<Member, Long>, MemberRepositoryCustom {

}
