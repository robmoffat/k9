package com.kite9.k9server.domain.project;

import org.springframework.data.repository.query.Param;

public interface ProjectRepositoryCustom {

	/**
	 * Handles save, ensures that the creator has privileges.
	 */
	public Project save(@Param("entity") Project r);
	
	/**
	 * Provided for internal methods to use.
	 */
	public Project saveInternal(Project entity);
	
}
