package com.kite9.k9server.domain.permission;

public interface MemberRepositoryCustom {

	/**
	 * Handles save, ensures that the creator has privileges.
	 */
	public Member save(Member r);
	
	/**
	 * Provided for internal methods to use.
	 */
	public Member saveInternal(Member entity);
	
}
