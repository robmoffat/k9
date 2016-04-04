package com.kite9.k9server.repos;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.BasePathAwareController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.kite9.k9server.domain.User;
import com.kite9.k9server.security.Hash;

@BasePathAwareController
public class UserController {

	private final UserRepository userRepository;
	
	@Autowired
	public UserController(UserRepository ur) {
		this.userRepository = ur;
	}
	
	@RequestMapping(path = "/users/create", method=RequestMethod.GET) 
    public @ResponseBody ResponseEntity<User> createUser(
    		@RequestParam(name="username") String username, 
    		@RequestParam(name="password") String password, 
    		@RequestParam(name="email") String email) {
		
		User existing = userRepository.findByEmail(email);
		if (existing != null) {
			return new ResponseEntity<User>(HttpStatus.CONFLICT);
		}
		
		String passwordHash = Hash.generatePasswordHash(password);
		
		User u = new User(username, passwordHash, email);
		userRepository.save(u);
		return ResponseEntity.ok(u);
	}
}
