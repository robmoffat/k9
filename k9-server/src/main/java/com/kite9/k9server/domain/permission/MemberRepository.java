package com.kite9.k9server.domain.permission;

import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Component;

import com.kite9.k9server.domain.SecuredCrudRepository;

@Component
@RepositoryRestResource(excerptProjection=MemberExcerptProjection.class)
public interface MemberRepository extends SecuredCrudRepository<Member>, MemberRepositoryCustom {

}
