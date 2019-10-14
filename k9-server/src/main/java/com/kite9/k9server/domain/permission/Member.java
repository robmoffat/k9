package com.kite9.k9server.domain.permission;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.kite9.k9server.domain.entity.AbstractLongIdEntity;
import com.kite9.k9server.domain.entity.RestEntity;
import com.kite9.k9server.domain.entity.Secured;
import com.kite9.k9server.domain.project.Project;
import com.kite9.k9server.domain.user.User;

@Entity
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
	
	@JsonIgnore
	@Override
	public String getIcon() {
		return user.getIcon();
	}

	@JsonIgnore
	@Override
	public String getTitle() {
		return user.getUsername();
	}

	@JsonIgnore
	@Override
	public String getDescription() {
		return "a b c d e gf g h i j k l m n o p q r s t u v w x y z"; //projectRole.toString().toLowerCase()+" in "+project.getTitle();
	}
	
	@JsonIgnore
	@Override
	public Date getLastUpdated() {
		return null;
	}
	
	@JsonIgnore
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
