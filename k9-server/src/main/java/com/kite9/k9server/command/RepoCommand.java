package com.kite9.k9server.command;

import org.springframework.data.repository.support.Repositories;

import com.kite9.k9server.domain.entity.RestEntity;
import com.kite9.k9server.domain.entity.RestEntityCrudRepository;

/**
 * The command needs access to repositories;
 */
public interface RepoCommand extends Command {
	
	public void setRepositories(Repositories r);
	
	public <X extends RestEntity> RestEntityCrudRepository<X> getRepositoryFor(Class<X> c);

}
