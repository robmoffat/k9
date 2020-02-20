package com.kite9.k9server.command.controllers;

import java.net.URI;
import java.util.List;

import org.kite9.framework.logging.Kite9Log;
import org.kite9.framework.logging.Logable;
import org.kohsuke.github.GHPerson;
import org.kohsuke.github.GHRepository;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;

import com.kite9.k9server.adl.holder.ADL;
import com.kite9.k9server.command.Command;
import com.kite9.k9server.command.GithubCommand;
import com.kite9.k9server.command.XMLCommand;
import com.kite9.k9server.domain.github.AbstractGithubController;

public abstract class AbstractCommandController extends AbstractGithubController implements Logable {

	Kite9Log log = new Kite9Log(this);
			
	public AbstractCommandController() {
		super();
	}

	public Object performSteps(List<Command> steps, Object input, Authentication a, HttpHeaders headers, URI url) throws Exception {
		for (Command command : steps) {
			if ((command instanceof XMLCommand) && (input != null)) {
				((XMLCommand) command).setOn((ADL) input);
			}
			
			if (command instanceof GithubCommand) {
				((GithubCommand) command).setGithubApi(apiFactory.createApiFor(a), headers, a);
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