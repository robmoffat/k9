package com.kite9.k9server.domain.document.commands;

import com.kite9.k9server.adl.holder.ADL;
import com.kite9.k9server.adl.holder.ADLImpl;
import com.kite9.k9server.command.CommandException;
import com.kite9.k9server.domain.document.Document;
import com.kite9.k9server.domain.revision.Revision;

public class Undo extends AbstractDocumentCommand {

	public Undo() {
		super();
	}

	public Undo(String fragmentId, String fragmentHash) {
		super(fragmentId, fragmentHash);
	}

	@Override
	public ADL applyCommand(ADL in) throws CommandException {
		validateFragmentHash(in);
		Revision rPrevious = currentRevision.getPreviousRevision();
		
		if (rPrevious == null) {
			throw new CommandException("No previous state to undo to", this);
		}
		
		Document d = currentRevision.getDocument();
		d.setCurrentRevision(rPrevious);
		repo.save(d); 
		
		this.currentRevision = rPrevious;
		
		return new ADLImpl(rPrevious.getXml(), url);
	}
	

}
