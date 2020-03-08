package com.kite9.k9server.command.content;

import org.springframework.security.core.Authentication;

public interface ContentAPIFactory<K> {

	/**
	 * Returns the api for working with some content.
	 * @throws Exception 
	 */
	public ContentAPI createAPI(Authentication a, String path) throws Exception;
}
