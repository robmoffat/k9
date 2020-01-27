package com.kite9.k9server.command.domain;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.kite9.k9server.command.AbstractSubjectCommand;
import com.kite9.k9server.command.CommandException;
import com.kite9.k9server.domain.document.Document;
import com.kite9.k9server.domain.entity.RestEntity;
import com.kite9.k9server.domain.entity.RestEntityCrudRepository;
import com.kite9.k9server.domain.entity.Secured;
import com.kite9.k9server.domain.permission.Member;
import com.kite9.k9server.domain.project.Project;
import com.kite9.k9server.domain.user.User;

public class DeleteEntity extends AbstractSubjectCommand<RestEntity>{

	@Override
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Object applyCommand() throws CommandException {
		if (current instanceof Secured) {
			if (!((Secured) current).checkDelete()) {
				throw new CommandException(HttpStatus.UNAUTHORIZED, "You don't have rights to delete this entity", this);
			}
		}

		RestEntityCrudRepository r = getRepositoryFor(current.getClass());
		
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
			
			if (current instanceof Document) {
				((Project)parent).getDocuments().remove(current);
			} else if (current instanceof Member) {
				((Project)parent).getMembers().remove(current);
			}
	
			
			return parent;
		}
		
	}

}
