package com.kite9.k9server.command.domain.revision;

import com.kite9.k9server.adl.holder.ADL;
import com.kite9.k9server.adl.holder.ADLImpl;
import com.kite9.k9server.command.CommandException;
import com.kite9.k9server.command.domain.AbstractDomainCommand;
import com.kite9.k9server.domain.document.Document;
import com.kite9.k9server.domain.document.DocumentRepositoryCustom;
import com.kite9.k9server.domain.revision.Revision;

public class Undo extends AbstractDomainCommand<Document, Document> implements RevisionCommand {

	public Undo() {
		super();
	}
	
	@Override
	public ADL applyCommand() throws CommandException {
		Revision rPrevious = getCurrentRevision().getPreviousRevision();
		
		if (rPrevious == null) {
			throw new CommandException("No previous state to undo to", this);
		}
		
		Document d = current;
		d.setCurrentRevision(rPrevious);
		((DocumentRepositoryCustom) repo).save(d); 
				
		return new ADLImpl(rPrevious.getXml(), uri, requestHeaders);
	}
	
	public Revision getCurrentRevision() {
		return current.getCurrentRevision();
	}

}
