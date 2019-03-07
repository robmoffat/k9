package com.kite9.k9server.domain.document;

import com.kite9.k9server.adl.holder.ADL;
import com.kite9.k9server.adl.holder.ADLImpl;
import com.kite9.k9server.command.CommandException;
import com.kite9.k9server.domain.revision.Revision;

public class Redo extends AbstractDocumentCommand {
	
	public Redo() {
		super();
	}

	public Redo(String fragmentId, String fragmentHash) {
		super(fragmentId, fragmentHash);
	}

	@Override
	public ADL applyCommand(ADL in) throws CommandException {
		validateFragmentHash(in);
		Revision rNext = currentRevision.getNextRevision();
		
		if (rNext == null) {
			throw new CommandException("No further state to redo to", this);
		}
		
		Document d = currentRevision.getDocument();
		d.setCurrentRevision(rNext);
		repo.save(d);
		return new ADLImpl(rNext.getXml(), url);
	}

}
