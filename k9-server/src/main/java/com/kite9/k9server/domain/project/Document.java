package com.kite9.k9server.domain.project;

import com.kite9.k9server.domain.entity.RestEntity;

public abstract class Document extends RestEntity<Document> {

	@Override
	public String getCommands() {
		return "edit view";
	}

}
