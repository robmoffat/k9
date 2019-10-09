package com.kite9.k9server.domain.permission;

import org.springframework.data.rest.core.config.Projection;

import com.kite9.k9server.domain.BasicExcerptProjection;
import com.kite9.k9server.domain.project.Project;
import com.kite9.k9server.domain.user.User;

@Projection(types={Member.class}, name="default")
public interface MemberExcerptProjection extends BasicExcerptProjection {

	Project getProject();
	
	User getUser();
	
	ProjectRole getProjectRole();
	
}
