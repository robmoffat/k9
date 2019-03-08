package com.kite9.k9server.domain.project;

public interface ProjectRepositoryCustom {

	/**
	 * Handles save, ensures that the creator has privileges.
	 */
	public Project save(Project r);
	
	/**
	 * Provided for internal methods to use.
	 */
	public Project saveInternal(Project entity);
	
}
