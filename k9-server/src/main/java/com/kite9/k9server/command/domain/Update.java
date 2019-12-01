package com.kite9.k9server.command.domain;

import com.kite9.k9server.command.AbstractSubjectCommand;
import com.kite9.k9server.command.CommandException;
import com.kite9.k9server.domain.entity.RestEntity;
import com.kite9.k9server.domain.entity.RestEntityCrudRepository;
import com.kite9.k9server.domain.entity.Updateable;

/**
 * This is going to update the fields on an entity, by applying a map of strings.
 * 
 * @author robmoffat
 *
 */
public class Update extends AbstractSubjectCommand<Updateable> {
	
	public String title;
	public String description;

	@Override
	@SuppressWarnings("unchecked")
	public RestEntity applyCommand() throws CommandException {
		if (title != null) {
			current.setTitle(title);
		}
		if (description != null) {
			current.setDescription(description);
		}
		RestEntityCrudRepository<Updateable> repo = (RestEntityCrudRepository<Updateable>) getRepositoryFor(current.getClass());
		repo.save(current);
		return current;
	}

}
