package com.kite9.k9server.domain.permission;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;

import com.kite9.k9server.domain.project.Project;
import com.kite9.k9server.domain.project.ProjectRole;
import com.kite9.k9server.domain.user.User;

public class Member {

	@Id
	private Project project;

	@Id
	private User user;

	@Enumerated(EnumType.STRING)
	private ProjectRole projectRole;

	public ProjectRole getProjectRole() {
		return projectRole;
	}

	public void setProjectRole(ProjectRole projectRole) {
		this.projectRole = projectRole;
	}

	public Member(Project project, ProjectRole pr, User user) {
		super();
		this.project = project;
		this.projectRole = pr;
		this.user = user;
	}

	public Project getProject() {
		return project;
	}

	public User getUser() {
		return user;
	}

}
