package com.kite9.k9server.command.domain.rest;

import org.springframework.http.ResponseEntity;

import com.kite9.k9server.command.CommandException;
import com.kite9.k9server.command.domain.AbstractRepoCommand;
import com.kite9.k9server.domain.RestEntity;
import com.kite9.k9server.domain.Secured;
import com.kite9.k9server.domain.SecuredCrudRepository;
import com.kite9.k9server.domain.user.User;

public class DeleteEntity extends AbstractRepoCommand<RestEntity>{

	@Override
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Object applyCommand() throws CommandException {
		if (current instanceof Secured) {
			if (!((Secured) current).checkDelete()) {
				throw new CommandException("You don't have rights to delete this entity", this);
			}
		}

		SecuredCrudRepository r = getRepositoryFor(current.getClass());
		
		if (current instanceof User) {
			((User) current).setEmail(null);
			String expiredName = ((User) current).getUsername()+"_exp_"+System.currentTimeMillis();
			((User) current).setUsername(expiredName.substring(0, Math.min(100, expiredName.length())));
			((User) current).setAccountExpired(true);
			r.save(current); 
			return ResponseEntity.noContent();
		} else {
			RestEntity parent = current.getParent();
			r.delete(current);
			return parent;
		}
		
	}

}
