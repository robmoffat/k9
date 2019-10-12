package com.kite9.k9server.command.domain.rest;

import java.util.List;

import com.kite9.k9server.command.CommandException;
import com.kite9.k9server.command.domain.AbstractRestCommand;
import com.kite9.k9server.domain.permission.Member;
import com.kite9.k9server.domain.permission.ProjectRole;
import com.kite9.k9server.domain.project.Project;

public class AddMember extends AbstractRestCommand<Project, Member> {
	
	protected List<String> emailAddresses;
	
	protected ProjectRole role;

	@Override
	public Project applyCommand() throws CommandException {
		Member n = new Member()
		
	}

}
