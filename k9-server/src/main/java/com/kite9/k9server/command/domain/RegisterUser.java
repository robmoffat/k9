package com.kite9.k9server.command.domain;

import org.springframework.data.repository.support.Repositories;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;

import com.kite9.k9server.command.AbstractSubjectCommand;
import com.kite9.k9server.command.CommandException;
import com.kite9.k9server.domain.entity.RestEntity;
import com.kite9.k9server.domain.entity.RestEntityCrudRepository;
import com.kite9.k9server.domain.user.User;
import com.kite9.k9server.domain.user.UserRepository;
import com.kite9.k9server.security.Hash;

public class RegisterUser extends AbstractSubjectCommand<User> {

	UserRepository ur;
	
	public String username;
	public String password;
	public String email;
	
	@Override
	public void setRepositories(Repositories r) {
		this.ur = (UserRepository) r.getRepositoryFor(User.class).get();
	}

	@Override
	public <X extends RestEntity> RestEntityCrudRepository<X> getRepositoryFor(Class<X> c) {
		return null;
	}

	@Override
	public Object applyCommand() throws CommandException {
		try {
			User out = new User();
			out.setEmail(email);
			out.setDisplayName(username);
			out.setPassword(Hash.generatePasswordHash(password));
			out.setSalt(User.createNewSalt());
			out.setEmailVerified(false);
			ur.save(out);
			
			UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(username, password);
			SecurityContextImpl context = new SecurityContextImpl(token);
			token.setDetails(out);
			SecurityContextHolder.setContext(context);
			
			return out;
		} catch (Exception e) {
			throw new CommandException(HttpStatus.CONFLICT, "Couldn't register user.  User may already be registered", e, this);
		}
	}
}
