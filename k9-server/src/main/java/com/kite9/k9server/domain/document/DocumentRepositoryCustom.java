package com.kite9.k9server.domain.document;

public interface DocumentRepositoryCustom {

	/**
	 * Handles save, ensures that the creator has privileges.
	 */
	public Document save(Document r);
	
	/**
	 * Provided for internal methods to use.
	 */
	public Document saveInternal(Document entity);
	
}
