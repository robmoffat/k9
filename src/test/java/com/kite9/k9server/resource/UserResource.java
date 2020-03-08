package com.kite9.k9server.resource;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kite9.k9server.domain.Organisation;
import com.kite9.k9server.domain.Repository;
import com.kite9.k9server.domain.User;

public class UserResource extends User {
	
	List<Organisation> organisations;
	List<Repository> repositories;
	String title;
	String description;
	String icon;

	@Override
	public List<Organisation> getOrganisations() {
		return organisations;
	}

	@Override
	public List<Repository> getRepositories() {
		return repositories;
	}

	@Override
	public String getTitle() {
		return title;
	}

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public String getIcon() {
		return icon;
	}

	
}
