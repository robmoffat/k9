package com.kite9.k9server.domain.permission;

import org.springframework.data.repository.query.Param;

public interface MemberRepositoryCustom {

	/**
	 * Handles save, ensures that the creator has privileges.
	 */
	public Member save(@Param("entity") Member r);
	
	/**
	 * Provided for internal methods to use.
	 */
	public Member saveInternal(Member entity);
	
}
