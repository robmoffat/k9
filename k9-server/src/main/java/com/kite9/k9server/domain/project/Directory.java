package com.kite9.k9server.domain.project;

import java.io.IOException;
import java.util.List;

import com.kite9.k9server.domain.entity.RestEntity;

public abstract class Directory extends RestEntity<Repository> {

	@Override
	public String getIcon() {
		return "";
	}

	@Override
	public String getType() {
		return "directory";
	}

	@Override
	public String getCommands() {
		return "focus";
	}

	public abstract List<Document> getDocuments() throws IOException;
	
	public abstract List<Directory> getSubDirectories() throws IOException;

}
