package com.kite9.k9server.command.revision;

import org.springframework.http.HttpStatus;

import com.kite9.k9server.adl.holder.ADL;
import com.kite9.k9server.adl.holder.ADLImpl;
import com.kite9.k9server.command.AbstractRepoCommand;
import com.kite9.k9server.command.CommandException;
import com.kite9.k9server.domain.document.Document;
import com.kite9.k9server.domain.revision.Revision;

public class Undo extends AbstractRepoCommand<Document> {

	public Undo() {
		super();
	}
	
	@Override
	public ADL applyCommand() throws CommandException {
		Revision rPrevious = getCurrentRevision().getPreviousRevision();
		
		if (rPrevious == null) {
			throw new CommandException(HttpStatus.FORBIDDEN, "No previous state to undo to", this);
		}
		
		Document d = current;
		d.setCurrentRevision(rPrevious);
		getRepositoryFor(Document.class).save(d); 
				
		return ADLImpl.xmlMode(uri, rPrevious.getXml(), requestHeaders);
	}
	
	public Revision getCurrentRevision() {
		return current.getCurrentRevision();
	}
}