package com.kite9.k9server.command.domain.rest;

import org.springframework.data.repository.support.Repositories;

import com.kite9.k9server.command.CommandException;
import com.kite9.k9server.command.domain.AbstractRepoCommand;
import com.kite9.k9server.domain.RestEntity;
import com.kite9.k9server.domain.SecuredCrudRepository;
import com.kite9.k9server.domain.user.User;
import com.kite9.k9server.domain.user.UserRepository;
import com.kite9.k9server.security.Hash;

public class RegisterUser extends AbstractRepoCommand<User> {

	UserRepository ur;
	
	public String username;
	public String password;
	public String email;
	
	@Override
	public void setRepositories(Repositories r) {
		this.ur = (UserRepository) r.getRepositoryFor(User.class).get();
	}

	@Override
	public <X extends RestEntity> SecuredCrudRepository<X> getRepositoryFor(Class<X> c) {
		return null;
	}

	@Override
	public Object applyCommand() throws CommandException {
		try {
			User out = new User();
			out.setEmail(email);
			out.setUsername(username);
			out.setPassword(Hash.generatePasswordHash(password));
			out.setSalt(User.createNewSalt());
			ur.save(out);
			return out;
		} catch (Exception e) {
			throw new CommandException("Couldn't register user.  User may already be registered", e, this);
		}
	}
}
