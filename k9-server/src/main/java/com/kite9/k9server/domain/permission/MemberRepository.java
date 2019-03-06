package com.kite9.k9server.domain.permission;

import org.springframework.stereotype.Component;

import com.kite9.k9server.domain.SecuredCrudRepository;

@Component
public interface MemberRepository extends SecuredCrudRepository<Member, Long> {

}
