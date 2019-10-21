package com.kite9.k9server.command;

import org.springframework.data.repository.support.Repositories;
import org.springframework.http.HttpStatus;

import com.kite9.k9server.domain.entity.RestEntity;
import com.kite9.k9server.domain.entity.RestEntityCrudRepository;

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
	public <X extends RestEntity> RestEntityCrudRepository<X> getRepositoryFor(Class<X> c) {
		Object out = repositories.getRepositoryFor(c).orElseThrow(() -> new CommandException(HttpStatus.NOT_FOUND, "No repository for "+c, this));
		return (RestEntityCrudRepository<X>) out;
	}
	
}
