package com.kite9.k9server.command.domain;

import java.util.List;

import org.springframework.http.HttpStatus;

import com.kite9.k9server.command.AbstractSubjectCommand;
import com.kite9.k9server.command.CommandException;
import com.kite9.k9server.domain.permission.Member;
import com.kite9.k9server.domain.permission.MemberRepository;
import com.kite9.k9server.domain.permission.ProjectRole;
import com.kite9.k9server.domain.project.Project;
import com.kite9.k9server.domain.user.User;
import com.kite9.k9server.domain.user.UserRepository;
import com.kite9.k9server.security.Hash;

public class AddMembers extends AbstractSubjectCommand<Project> {
	
	public String emailAddresses;
	
	public ProjectRole role = ProjectRole.MEMBER;

	@Override
	public Project applyCommand() throws CommandException {
		List<Member> existingMembers = current.getMembers();
		UserRepository users = (UserRepository) getRepositoryFor(User.class);
		MemberRepository members = (MemberRepository) getRepositoryFor(Member.class);
		
		for (String e : emailAddresses.split(",")) {
			e=e.strip();
			Member m = getExistingMember(e, existingMembers);
			if (m == null) {
				User u = users.findByEmail(e);
				
				if (u == null) {
					String username = e.substring(0, e.indexOf("@")-1);
					u = new User(username, null, e);
					u.setPasswordExpired(true);
					users.save(u);
					
				}
				
				m = new Member(current, role, u);
				current.getMembers().add(m);
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
