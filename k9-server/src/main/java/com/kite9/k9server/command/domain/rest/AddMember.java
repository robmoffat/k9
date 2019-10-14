package com.kite9.k9server.command.domain.rest;

import java.util.List;

import com.kite9.k9server.command.CommandException;
import com.kite9.k9server.command.domain.AbstractRepoCommand;
import com.kite9.k9server.domain.permission.Member;
import com.kite9.k9server.domain.permission.MemberRepository;
import com.kite9.k9server.domain.permission.ProjectRole;
import com.kite9.k9server.domain.project.Project;
import com.kite9.k9server.domain.user.User;
import com.kite9.k9server.domain.user.UserRepository;

public class AddMember extends AbstractRepoCommand<Project> {
	
	protected List<String> emailAddresses;
	
	protected ProjectRole role;

	@Override
	public Project applyCommand() throws CommandException {
		List<Member> existingMembers = current.getMembers();
		UserRepository users = (UserRepository) getRepositoryFor(User.class);
		MemberRepository members = (MemberRepository) getRepositoryFor(Member.class);
		
		for (String e : emailAddresses) {
			Member m = getExistingMember(e, existingMembers);
			if (m == null) {
				User u = users.findByEmail(e);
				
				if (u == null) {
					throw new CommandException("Not implemented adding members where they aren't part of the project");
				}
				
				m = new Member(current, role, u);
			}
			m.setProjectRole(role);
			members.save(m);
		}

		return current;
	}

	private Member getExistingMember(String e, List<Member> existingMembers) {
		return existingMembers.stream()
			.filter(m -> m.getUser().getEmail().equals(e))
			.findFirst()
			.orElse(null);
	}

}
