package com.kite9.k9server.domain.document;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetailsService;

import com.kite9.k9server.web.HttpException;

public class DocumentRepositoryImpl implements DocumentRepositoryCustom {

	@Autowired
	DocumentRepository self;
	
	@Autowired
	UserDetailsService userDetails;
	
	@Override
	public Document save(Document r) {
		if (!r.checkWrite()) {
			throw new HttpException(HttpStatus.FORBIDDEN);
		}
		
		return self.saveInternal(r);
	}

	@Override
	public Document saveInternal(Document entity) {
		return self.saveAll(Collections.singleton(entity)).iterator().next();

	}

}
