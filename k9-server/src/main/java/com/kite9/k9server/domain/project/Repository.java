package com.kite9.k9server.domain.project;

import java.util.Date;
import java.util.List;

import com.kite9.k9server.domain.entity.RestEntity;

public abstract class Repository extends RestEntity<Repository> {
	
	@Override
	public String getIcon() {
		return "/public/context/admin/icons/project.svg";
	}

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
		return "project";
	}

	@Override
	public String getCommands() {
		return "NewDocument";
	}

	public abstract List<Document> getDocuments();
}
