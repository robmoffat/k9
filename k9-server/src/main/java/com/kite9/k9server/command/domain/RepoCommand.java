package com.kite9.k9server.command.domain;

import org.springframework.data.repository.support.Repositories;

import com.kite9.k9server.command.Command;
import com.kite9.k9server.domain.RestEntity;
import com.kite9.k9server.domain.SecuredCrudRepository;

/**
 * The command needs access to repositories;
 */
public interface RepoCommand extends Command {
	
	public void setRepositories(Repositories r);
	
	public <X extends RestEntity> SecuredCrudRepository<X> getRepositoryFor(Class<X> c);

}
