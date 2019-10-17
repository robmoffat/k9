package com.kite9.k9server.command.revision;

import org.springframework.http.HttpStatus;

import com.kite9.k9server.adl.holder.ADL;
import com.kite9.k9server.adl.holder.ADLImpl;
import com.kite9.k9server.command.AbstractRepoCommand;
import com.kite9.k9server.command.CommandException;
import com.kite9.k9server.domain.document.Document;
import com.kite9.k9server.domain.revision.Revision;

public class Redo extends AbstractRepoCommand<Document> {
	
	public Redo() {
		super();
	}

	@Override
	public ADL applyCommand() throws CommandException {
		Revision rNext = getCurrentRevision().getNextRevision();
		
		if (rNext == null) {
			throw new CommandException(HttpStatus.FORBIDDEN, "No further state to redo to", this);
		}
		
		Document d = current;
		d.setCurrentRevision(rNext);
		getRepositoryFor(Document.class).save(d);
		
		return ADLImpl.xmlMode(uri, rNext.getXml(), requestHeaders);
	}

	public Revision getCurrentRevision() {
		return current.getCurrentRevision();
	}
}
