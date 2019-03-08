package com.kite9.k9server.domain.project;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.client.HttpClientErrorException;

import com.kite9.k9server.domain.permission.Member;
import com.kite9.k9server.domain.permission.ProjectRole;
import com.kite9.k9server.domain.user.User;

public class ProjectRepositoryImpl implements ProjectRepositoryCustom {

	@Autowired
	ProjectRepository self;
	
	@Autowired
	UserDetailsService userDetails;
	
	@Override
	public Project save(Project r) {
		
		if (r.getId() == null) {
			// it's new
			List<Member> members = r.getMembers();
			
			if (members.size() == 0) {
				// create the default member
				Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
				if (authentication != null) {
					String userName = authentication.getName();
					User u = (User) userDetails.loadUserByUsername(userName);
					Member m = new Member(r, ProjectRole.ADMIN, u);
					members.add(m);
				}
			}
		} else if (!r.checkWrite()) {
			throw new HttpClientErrorException(HttpStatus.FORBIDDEN);
		}
		
		return self.saveInternal(r);
	}

	@Override
	public Project saveInternal(Project entity) {
		return self.saveAll(Collections.singleton(entity)).iterator().next();

	}

}
