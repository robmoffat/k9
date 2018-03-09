package com.kite9.k9server.domain.revision;

public interface RevisionRepositoryCustom {

	/**
	 * Handles current/next revision information on the document.
	 */
	public Revision save(Revision r);
	
	/**
	 * Provided for internal methods to use.
	 */
	public Revision saveInternal(Revision entity);
}
