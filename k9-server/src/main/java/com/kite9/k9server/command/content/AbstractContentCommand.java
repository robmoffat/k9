package com.kite9.k9server.command.content;

import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;

import com.kite9.k9server.adl.format.FormatSupplier;

public abstract class AbstractContentCommand implements ContentCommand {

	protected String subjectUri;
	protected transient ContentAPI api;
	protected transient HttpHeaders requestHeaders;
	protected transient Authentication a;
	protected transient FormatSupplier fs;
	
	@Override
	public void setContentApi(ContentAPI api, HttpHeaders requestHeaders, Authentication a, FormatSupplier fs) {
		this.api = api;
		this.requestHeaders = requestHeaders;
		this.a = a;
		this.fs = fs;
	}
	
	
}
