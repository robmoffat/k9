package com.kite9.k9server.resource;

import org.springframework.hateoas.ResourceSupport;

import com.kite9.k9server.domain.permission.ProjectRole;

public class MemberResource extends ResourceSupport {

	public String user;
	public String project;
	public ProjectRole projectRole;
	
	public MemberResource() {
		super();
	}

	public MemberResource(String user, String project, ProjectRole projectRole) {
		super();
		this.user = user;
		this.project = project;
		this.projectRole = projectRole;
	}
	
	
	
}
