package com.kite9.k9server.command.domain.rest;

import com.kite9.k9server.command.CommandException;
import com.kite9.k9server.command.domain.AbstractRepoCommand;
import com.kite9.k9server.domain.entity.RestEntity;
import com.kite9.k9server.domain.entity.SecuredCrudRepository;
import com.kite9.k9server.domain.entity.Updateable;

/**
 * This is going to update the fields on an entity, by applying a map of strings.
 * 
 * @author robmoffat
 *
 */
public class Update extends AbstractRepoCommand<Updateable> {
	
	public String newTitle;
	public String newDescription;

	@Override
	@SuppressWarnings("unchecked")
	public RestEntity applyCommand() throws CommandException {
		if (newTitle != null) {
			current.setTitle(newTitle);
		}
		if (newDescription != null) {
			current.setDescription(newDescription);
		}
		SecuredCrudRepository<Updateable> repo = (SecuredCrudRepository<Updateable>) getRepositoryFor(current.getClass());
		repo.save(current);
		return current;
	}

}
