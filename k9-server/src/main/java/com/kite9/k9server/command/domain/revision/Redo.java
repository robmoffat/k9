package com.kite9.k9server.command.domain.revision;

import com.kite9.k9server.adl.holder.ADL;
import com.kite9.k9server.adl.holder.ADLImpl;
import com.kite9.k9server.command.CommandException;
import com.kite9.k9server.command.domain.AbstractDomainCommand;
import com.kite9.k9server.command.domain.AbstractRepoCommand;
import com.kite9.k9server.domain.document.Document;
import com.kite9.k9server.domain.document.DocumentRepositoryCustom;
import com.kite9.k9server.domain.revision.Revision;

public class Redo extends AbstractRepoCommand<Document> implements RevisionCommand {
	
	public Redo() {
		super();
	}

	@Override
	public ADL applyCommand() throws CommandException {
		Revision rNext = getCurrentRevision().getNextRevision();
		
		if (rNext == null) {
			throw new CommandException("No further state to redo to", this);
		}
		
		Document d = current;
		d.setCurrentRevision(rNext);
		getRepositoryFor(Document.class).save(d);
		
		return new ADLImpl(rNext.getXml(), uri, requestHeaders);
	}

	public Revision getCurrentRevision() {
		return current.getCurrentRevision();
	}
}
