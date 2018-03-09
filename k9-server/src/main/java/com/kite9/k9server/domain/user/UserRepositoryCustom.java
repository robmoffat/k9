package com.kite9.k9server.domain.user;

/**
 * Custom repository method for saving & deleting.
 * 
 * @author robmoffat
 *
 */
public interface UserRepositoryCustom {

	/**
	 * Public API - doesn't need security. You can call this with curl like:
	 * 
	 * <pre>
	 * curl -v -H "Content-Type: application/json" -d '{ "username" : "bob", "password": "pass", "email" : "rob@kite9.com" }' http://localhost:8080/api/users
	 * </pre>
	 */
	public <X extends User> X save(User entity);
	
	/**
	 * Provided for internal methods to use, no security.
	 */
	public User saveInternal(User entity);
	
	/**
	 * Removes the user permanently.  
	 * If the user can't be deleted, their account is set to expired.
	 * e.g. curl -v -H "Authorization: KITE9 some-code" -X "DELETE" http://localhost:8080/api/users/3
	 */
	public void delete(Long id);

}
