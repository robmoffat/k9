package com.kite9.k9server.command.domain.rest;

import com.kite9.k9server.command.CommandException;
import com.kite9.k9server.command.domain.AbstractRepoCommand;
import com.kite9.k9server.domain.RestEntity;
import com.kite9.k9server.domain.user.User;

public class NewProject extends AbstractRepoCommand<User> {

	String name;
	
	String description;

	@Override
	public RestEntity applyCommand() throws CommandException {
		
	}
}
