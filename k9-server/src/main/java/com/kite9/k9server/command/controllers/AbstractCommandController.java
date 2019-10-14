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
import com.kite9.k9server.command.domain.DomainCommand;
import com.kite9.k9server.command.domain.RepoCommand;
import com.kite9.k9server.command.xml.XMLCommand;
import com.kite9.k9server.domain.entity.RestEntity;

public abstract class AbstractCommandController implements Logable {

	Kite9Log log = new Kite9Log(this);

	@Autowired
	Repositories repositories;
	
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

	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected Object performSteps(List<Command> steps, Object input, RestEntity context, HttpHeaders headers, URI url) {
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
}