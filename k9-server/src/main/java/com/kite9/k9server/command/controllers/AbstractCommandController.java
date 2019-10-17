package com.kite9.k9server.command.controllers;

import java.net.URI;
import java.util.List;

import org.kite9.framework.logging.Kite9Log;
import org.kite9.framework.logging.Logable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.support.Repositories;
import org.springframework.hateoas.EntityLinks;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.kite9.k9server.adl.holder.ADL;
import com.kite9.k9server.command.Command;
import com.kite9.k9server.command.DomainCommand;
import com.kite9.k9server.command.RepoCommand;
import com.kite9.k9server.command.XMLCommand;
import com.kite9.k9server.domain.entity.RestEntity;
import com.kite9.k9server.domain.entity.Secured;

public abstract class AbstractCommandController implements Logable {

	Kite9Log log = new Kite9Log(this);

	@Autowired
	Repositories repositories;
	
	@Autowired
	EntityLinks entityLinks;
		

	public AbstractCommandController() {
		super();
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Transactional
	protected Object performSteps(List<Command> steps, Object input, RestEntity context, HttpHeaders headers, URI url) {
		checkDomainAccess(context, url);
		for (Command command : steps) {
			if (command instanceof XMLCommand) {
				((XMLCommand) command).setOn((ADL) input);
			}
			
			if (command instanceof DomainCommand) {
				((DomainCommand)command).setCommandContext(context, url, headers);
			}
			
			if (command instanceof RepoCommand) {
				((RepoCommand)command).setRepositories(repositories);
			}
		
			input = command.applyCommand();
		}
		return input;
	}

	@Override
	public String getPrefix() {
		return "SCC ";
	}

	@Override
	public boolean isLoggingEnabled() {
		return true;
	}

	protected void checkDomainAccess(RestEntity d, URI url) {
		if (d instanceof Secured) {
			if (!((Secured) d).checkWrite()) {
				throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "No write access for "+url);
			}
		}
	}
}