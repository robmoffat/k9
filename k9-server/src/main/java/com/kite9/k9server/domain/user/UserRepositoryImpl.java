package com.kite9.k9server.domain.user;

import java.util.Collections;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import com.kite9.k9server.security.Hash;
import com.kite9.k9server.web.HttpException;

/**
 * Handles save and delete custom code.
 * 
 * @author robmoffat
 *
 */
public class UserRepositoryImpl implements UserRepositoryCustom {
	
	@Autowired
	UserRepository userRepository;
	
	private static final Log LOG = LogFactory.getLog(UserRepositoryImpl.class);
	
	@SuppressWarnings("unchecked")
	@Override
	public User save(User newUser) {
		String email = newUser.getEmail();
		String password = newUser.getPassword();
		User u = userRepository.findByEmail(email);
		String passwordHash = Hash.generatePasswordHash(password);

		if (u != null) {
			ensureCorrectUserLoggedIn(email);
			u.update(newUser);
		} else {
			u = newUser;
			u.setPassword(passwordHash);
			u.setSalt(User.createNewSalt());
		}

		userRepository.saveInternal(u);
		return u;
	}

	private void ensureCorrectUserLoggedIn(String email) {
		// in this case, we might be saving the user.  
		// So, we need to check that the user matches the logged-in user.
		SecurityContext sc = SecurityContextHolder.getContext();
		if (!sc.getAuthentication().getPrincipal().equals(email)) {
			throw new HttpException(HttpStatus.CONFLICT, "Not your user");
		}
	}

//	@Override
//	public void deleteById(Long id) {
//		// this should always work
//		userRepository.expire(id);
//		try {
//			// this works if the user isn't on any projects.
//			userRepository.remove(id);
//		} catch (Exception e) {
//			LOG.info("User can't be deleted outright, expiring: "+id);
//		}
//	}

	@Override
	public User saveInternal(User entity) {
		return userRepository.saveAll(Collections.singleton(entity)).iterator().next();
	}
}
