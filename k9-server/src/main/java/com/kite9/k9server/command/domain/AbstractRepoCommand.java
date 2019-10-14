package com.kite9.k9server.command.domain;

import org.springframework.data.repository.support.Repositories;

import com.kite9.k9server.command.CommandException;
import com.kite9.k9server.domain.entity.RestEntity;
import com.kite9.k9server.domain.entity.SecuredCrudRepository;

/**
 * These commands return back a {@link RestEntity} object at the end.
 */
public abstract class AbstractRepoCommand<C extends RestEntity> extends AbstractDomainCommand<C> implements RepoCommand {

	protected Repositories repositories;

	@Override
	public void setRepositories(Repositories r) {
		this.repositories = r;
	}


	@SuppressWarnings("unchecked")
	@Override
	public <X extends RestEntity> SecuredCrudRepository<X> getRepositoryFor(Class<X> c) {
		Object out = repositories.getRepositoryFor(c).orElseThrow(() -> new CommandException("No repository for "+c, this));
		return (SecuredCrudRepository<X>) out;
	}
	
}
