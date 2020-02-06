package com.kite9.k9server.domain.project;

import java.util.Date;
import java.util.List;

import com.kite9.k9server.domain.entity.RestEntity;

public abstract class User extends RestEntity<User> {

	@Override
	public Date getLastUpdated() {
		return null;
	}
	
	@Override
	public RestEntity<?> getParent() {
		return null;
	}

	@Override
	public String getType() {
		return "user";
	}

	public abstract List<Organisation> getOrganisations();
	
	public abstract List<Repository> getRepositories();
	
}
