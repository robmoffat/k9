package com.kite9.k9server.domain.permission;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import com.kite9.k9server.command.domain.DeleteEntity;
import com.kite9.k9server.command.domain.WithCommands;
import com.kite9.k9server.domain.entity.AbstractLongIdEntity;
import com.kite9.k9server.domain.entity.RestEntity;
import com.kite9.k9server.domain.entity.Secured;
import com.kite9.k9server.domain.project.Project;
import com.kite9.k9server.domain.user.User;

@Entity
@WithCommands({DeleteEntity.class})
public class Member extends AbstractLongIdEntity implements Secured, MemberExcerptProjection {

	@ManyToOne(targetEntity = Project.class, optional = false, fetch = FetchType.LAZY)
	private Project project;

	@ManyToOne(targetEntity = User.class, optional = false, fetch = FetchType.EAGER)
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
	
	@JsonProperty(access = Access.READ_ONLY)
	@Override
	public String getIcon() {
		return user.getIcon();
	}

	@JsonProperty(access = Access.READ_ONLY)
	@Override
	public String getTitle() {
		return user.getUsername();
	}

	@JsonProperty(access = Access.READ_ONLY)
	@Override
	public String getDescription() {
		return projectRole.toString().toLowerCase()+" in "+project.getTitle();
	}
	
	@JsonProperty(access = Access.READ_ONLY)
	@Override
	public Date getLastUpdated() {
		return null;
	}
	
	@Override
	public RestEntity getParent() {
		return project;
	}

	public void setProject(Project project) {
		this.project = project;
	}

	public void setUser(User user) {
		this.user = user;
	}
	
	
}
