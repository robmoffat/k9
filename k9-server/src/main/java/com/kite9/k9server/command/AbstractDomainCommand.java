package com.kite9.k9server.command;

import java.net.URI;

import org.springframework.http.HttpHeaders;

import com.kite9.k9server.domain.entity.RestEntity;

public abstract class AbstractDomainCommand<C extends RestEntity> implements DomainCommand<C> {

	protected C current;
	protected URI uri;
	protected HttpHeaders requestHeaders;
	
	@Override
	public void setCommandContext(C current, URI url, HttpHeaders requestHeaders) {
		this.current = current;
		this.uri = url;
		this.requestHeaders = requestHeaders;
	}

}
