package com.kite9.k9server.command.domain;

import java.net.URI;

import org.springframework.http.HttpHeaders;

import com.kite9.k9server.domain.SecuredCrudRepository;

public abstract class AbstractDomainCommand<X> implements DomainCommand<X> {

	protected SecuredCrudRepository<X> repo;
	protected X current;
	protected URI uri;
	protected HttpHeaders requestHeaders;
	
	@Override
	public <Y extends SecuredCrudRepository<X>> void setCommandContext(Y repo, X current, URI url, HttpHeaders requestHeaders) {
		this.repo = repo;
		this.current = current;
		this.uri = url;
		this.requestHeaders = requestHeaders;
	}
	
	
}
