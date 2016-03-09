package com.kite9.k9server.repos;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.stereotype.Component;

import com.kite9.k9server.domain.User;

/**
 * This repository is not exported over REST, because there are too many things you can do to users
 * that we want to prevent.
 * 
 * @author robmoffat
 *
 */
@Component
@RestResource(exported=false)
public interface UserRepository extends CrudRepository<User, String> {

}
