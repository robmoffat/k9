package com.kite9.k9server.command;

import java.net.URI;

import org.springframework.http.HttpHeaders;

import com.kite9.k9server.domain.entity.RestEntity;

public abstract class AbstractDomainCommand implements ContextCommand {

	protected RestEntity context;
	protected URI uri;
	protected HttpHeaders requestHeaders;
	
	@Override
	public void setCommandContext(RestEntity context, URI url, HttpHeaders requestHeaders) {
		this.context = context;
		this.uri = url;
		this.requestHeaders = requestHeaders;
	}

}
