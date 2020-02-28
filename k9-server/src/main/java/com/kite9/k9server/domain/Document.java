package com.kite9.k9server.domain;

public abstract class Document extends RestEntity<Document> {

	@Override
	public String getCommands() {
		return "open";
	}

}
