package com.kite9.k9server.command.controllers;

import java.net.URI;
import java.util.List;

import org.kite9.framework.logging.Kite9Log;
import org.kite9.framework.logging.Logable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.support.Repositories;
import org.springframework.hateoas.EntityLinks;
import org.springframework.http.HttpHeaders;

import com.kite9.k9server.adl.holder.ADL;
import com.kite9.k9server.command.Command;
import com.kite9.k9server.command.CommandException;
import com.kite9.k9server.command.domain.DomainCommand;
import com.kite9.k9server.command.xml.XMLCommand;
import com.kite9.k9server.domain.RestEntity;
import com.kite9.k9server.domain.SecuredCrudRepository;
import com.kite9.k9server.domain.document.DocumentRepository;
import com.kite9.k9server.domain.revision.RevisionRepository;

public abstract class AbstractCommandController implements Logable {

	Kite9Log log = new Kite9Log(this);

	@Autowired
	Repositories repositories;
	
	@Autowired
	RevisionRepository revisionRepository;
	
	@Autowired
	DocumentRepository documentRepository;
	
	@Autowired
	EntityLinks entityLinks;
		

	public AbstractCommandController() {
		super();
	}

	protected ADL performXMLCommands(List<Command> steps, ADL input, RestEntity context, HttpHeaders headers, URI url) {
		
		if (log.go()) {
			log.send("Before: " + input.getAsXMLString());
		}
		
		input = (ADL) performSteps(steps, input, context, headers, url);
		checkRenderable(input);
		
		if (log.go()) {
			log.send("After: " + input.getAsXMLString());
		}
		
		return input;
	}

	protected void checkRenderable(ADL input) {
		input.getSVGRepresentation();
	}

	protected Object performSteps(List<Command> steps, Object input, RestEntity context, HttpHeaders headers, URI url) {
		for (Command command : steps) {
			if (command instanceof XMLCommand) {
				((XMLCommand) command).setOn((ADL) input);
			}
			
			if (command instanceof DomainCommand) {
				Object repo = repositories.getRepositoryFor(context.getClass()).orElseThrow(() -> new CommandException("No repository for "+context.getClass(), command));
				((DomainCommand)command).setCommandContext((SecuredCrudRepository) repo, context, url, headers);
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
}