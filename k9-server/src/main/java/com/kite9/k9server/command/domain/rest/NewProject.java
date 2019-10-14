package com.kite9.k9server.command.domain.rest;

import com.kite9.k9server.command.CommandException;
import com.kite9.k9server.command.domain.AbstractRepoCommand;
import com.kite9.k9server.domain.entity.RestEntity;
import com.kite9.k9server.domain.permission.Member;
import com.kite9.k9server.domain.permission.ProjectRole;
import com.kite9.k9server.domain.project.Project;
import com.kite9.k9server.domain.user.User;

public class NewProject extends AbstractRepoCommand<User> {

	public String title;
	
	public String description;
	
	public String stub;

	@Override
	public RestEntity applyCommand() throws CommandException {
		
		Project p = new Project();
		p.setDescription(description);
		p.setTitle(title);
		p.setStub(stub);
		
		getRepositoryFor(Project.class).save(p);
		
		Member m = new Member(p, ProjectRole.ADMIN, current);
		getRepositoryFor(Member.class).save(m);
		
		return p;
	}
}
