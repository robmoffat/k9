package com.kite9.k9server.command.controllers;

import java.net.URI;
import java.util.Base64;
import java.util.List;

import org.kite9.framework.logging.Kite9Log;
import org.kite9.framework.logging.Logable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.server.ResponseStatusException;

import com.kite9.k9server.adl.holder.ADL;
import com.kite9.k9server.adl.holder.ADLImpl;
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
			if (command instanceof XMLCommand) {
				XMLCommand xmlCommand = (XMLCommand) command;

				if (input == null) {
					if (xmlCommand.getBase64EncodedState() != null) {
						String base64 = xmlCommand.getBase64EncodedState();
						
						if (base64 == null) {
							throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No input to work with");
						}
						
						String adl = new String(Base64.getDecoder().decode(base64.getBytes()));
						input = ADLImpl.xmlMode(url, adl, headers);
					} else {
						input = ADLImpl.uriMode(url, headers);
					}
				}
				
				xmlCommand.setOn((ADL) input);
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