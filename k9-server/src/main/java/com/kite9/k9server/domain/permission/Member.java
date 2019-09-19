package com.kite9.k9server.domain.permission;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;

import com.kite9.k9server.domain.AbstractLongIdEntity;
import com.kite9.k9server.domain.Secured;
import com.kite9.k9server.domain.project.Project;
import com.kite9.k9server.domain.user.User;

@Entity
public class Member extends AbstractLongIdEntity implements Secured {

	@ManyToOne(targetEntity = Project.class, optional = false, fetch = FetchType.LAZY)
	private Project project;

	@ManyToOne(targetEntity = User.class, optional = false, fetch = FetchType.LAZY)
	private User user;

	@Enumerated(EnumType.STRING)
	private ProjectRole projectRole;

	public ProjectRole getProjectRole() {
		return projectRole;
	}

	public void setProjectRole(ProjectRole projectRole) {
		this.projectRole = projectRole;
	}
	
	public Member() {
	}

	public Member(Project project, ProjectRole pr, User user) {
		super();
		this.project = project;
		this.user = user;
		this.projectRole = pr;
	}

	public Project getProject() {
		return project;
	}

	public User getUser() {
		return user;
	}

	@Override
	public boolean checkAccess(Action a) {
		if (project == null) {
			return true;
		}
		
		// you can't write members unless admin
		a = (a == Action.WRITE) ? Action.ADMIN : a;
		
		return project.checkAccess(a);
		
	}
	
	@Override
	public String getLocalImagePath() {
		return "/public/admin/icons/member.svg";
	}

	@Override
	public String getTitle() {
		return user.getUsername();
	}

	@Override
	public String getDescription() {
		return projectRole.toString().toLowerCase();
	}
	
}
