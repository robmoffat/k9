package com.kite9.k9server.domain.user;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.transaction.annotation.Transactional;

import com.kite9.k9server.security.auth.UserAuthenticationProvider;

/**
 * Provides a publically-accessible repository for performing user creation, email validation and 
 * password changing.  
 * 
 * Since you can only affect your own user, this interface is heavily locked-down.
 * 
 * @author robmoffat
 *
 */
public interface UserRepository extends Repository<User, Long>, UserRepositoryCustom {
	
	/**
	 * Used by the {@link UserAuthenticationProvider}.
	 * Not to be exported, as it is not secure. 
	 */
	@RestResource(exported=false)
	public User findByApi(String api);
	
	/**
	 * Used by the {@link UserRepositoryImpl} and {@link UserAuthenticationProvider}
	 * Not to be exported, as it is not secure.
	 */
	@RestResource(exported=false)
	public User findByEmail(String email);
	
	/**
	 * Re-declaration of the method from {@link UserRepositoryCustom}, to hook it up.
	 */
	@SuppressWarnings("unchecked")
	public User save(User entity);
	
	/**
	 * Used internally only.
	 */
	@RestResource(exported=false)
	Iterable<User> save(Iterable<User> entities);
		
	/**
	 * This returns just your (logged in) user
	 */
	@Query("select u from User u where u.email = ?#{ principal }")
	public Iterable<User> findAll();
	
	/**
	 * Returns your (logged in) user only. 
	 */
	@Query("select u from User u where u.id = :id and u.email = ?#{principal}")
	public User findOne(@Param("id") Long id);

	/**
	 * Removes the user permanently.
	 */
	@Transactional
	@RestResource(exported=false)
	@Modifying(clearAutomatically=true)
	@Query("delete from User where id = :id  and email = ?#{principal}")
	void remove(@Param("id") Long id);
	
	/**
	 * Expires the user account.
	 */
	@Transactional
	@RestResource(exported=true)
	@Modifying(clearAutomatically=true)
	@Query("update User u set u.accountExpired = true where id = :id and email = ?#{principal}")
	void expire(@Param("id") Long id);
	
	/**
	 * Re-declaration of the method from {@link UserRepositoryCustom}, to hook it up.
	 */
	public void delete(Long id);
}
