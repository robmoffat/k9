package com.kite9.k9server.domain.user;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;

import com.kite9.k9server.domain.entity.RestEntityCrudRepository;
import com.kite9.k9server.security.OAuth2AuthorizationServerConfig;
import com.kite9.k9server.security.UserAuthenticationProvider;

/**
 * Provides a publically-accessible repository for performing user creation, email validation and 
 * password changing.  
 * 
 * Since you can only affect your own user, this interface is heavily locked-down.
 * 
 * @author robmoffat
 *
 */
@RepositoryRestResource(excerptProjection=UserMainProjection.class)
public interface UserRepository extends RestEntityCrudRepository<User> {
	
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
	 * Used by {@link UserAuthenticationProvider} and {@link OAuth2AuthorizationServerConfig}
	 * Not to be exported, as it is not secure.
	 */
	@RestResource(exported=false)
	public User findByUsername(String username);
	
	@Override
	@Query("select u from User u where u.accountExpired = false")
	public Iterable<User> findAll();
	
	@Override
	@Query( "select u from User u where u.id in :ids and u.accountExpired = false")
	public Iterable<User> findAllById(Iterable<Long> ids);

}
