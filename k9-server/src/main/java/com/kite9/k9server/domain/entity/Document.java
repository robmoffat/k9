package com.kite9.k9server.domain.entity;

public abstract class Document extends RestEntity<Document> {

	@Override
	public String getCommands() {
		return "edit view";
	}

}
