package com.kite9.k9server.domain.entity;

/**
 * Provides a method on the entity which allows it to say whether the logged-in
 * user is able to change/read/delete it.
 * 
 * @author robmoffat
 *
 */
public interface Secured {
	
	public enum Action { READ, WRITE, ADMIN }

	public boolean checkAccess(Action a);
	
	public default boolean checkRead() {
		return checkAccess(Action.READ);
	}
	
	public default boolean checkWrite() {
		return checkAccess(Action.WRITE);
	}
	
	public default boolean checkDelete() {
		return checkAccess(Action.ADMIN);
	}	
	
}
