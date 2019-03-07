package com.kite9.k9server.domain.permission;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.client.HttpClientErrorException;

import com.kite9.k9server.domain.Secured.Action;

public class MemberRepositoryImpl implements MemberRepositoryCustom {

	@Autowired
	MemberRepository self;
	
	@Autowired
	UserDetailsService userDetails;
	
	@Override
	public Member save(Member r) {
		if (!r.checkAccess(Action.ADMIN)) {
			throw new HttpClientErrorException(HttpStatus.FORBIDDEN);
		}
		
		return self.saveInternal(r);
	}

	@Override
	public Member saveInternal(Member entity) {
		return self.saveAll(Collections.singleton(entity)).iterator().next();

	}

}
