package com.kite9.k9server.command.domain;

import org.springframework.http.HttpStatus;

import com.kite9.k9server.command.AbstractSubjectCommand;
import com.kite9.k9server.command.CommandException;
import com.kite9.k9server.domain.entity.RestEntity;
import com.kite9.k9server.domain.permission.Member;
import com.kite9.k9server.domain.permission.ProjectRole;
import com.kite9.k9server.domain.project.Project;
import com.kite9.k9server.domain.user.User;

public class NewProject extends AbstractSubjectCommand<User> {

	public String title;
	
	public String description;
	
	public String stub;

	@Override
	public RestEntity applyCommand() throws CommandException {
		if (!current.checkWrite()) {
			throw new CommandException(HttpStatus.UNAUTHORIZED, "User can't write to "+current, this);
		}
		
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
