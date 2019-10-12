package com.kite9.k9server.command.domain.rest;

import java.util.Collections;

import com.kite9.k9server.command.CommandException;
import com.kite9.k9server.command.domain.AbstractRestCommand;
import com.kite9.k9server.domain.document.Document;
import com.kite9.k9server.domain.project.Project;

public class NewDocument extends AbstractRestCommand<Project> {
	
	protected String title;
	
	protected String description;
	
	protected String templateUri;
	
	@Override
	public Document applyCommand() throws CommandException {
		if (!current.checkWrite()) {
			throw new CommandException("User can't write to "+current, this);
		}
		
		Document out = new Document();
		out.setTitle(title);
		out.setDescription(description);
		out.setProject(current);
		
		getRepositoryFor(Document.class).saveAll(entities)
		
		repo.saveAll(Collections.singleton(out));
		
		// now we need 
		
		
		return out;
	}

}
