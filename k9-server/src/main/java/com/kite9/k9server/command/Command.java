package com.kite9.k9server.command;

import com.kite9.k9server.domain.Document;
import com.kite9.k9server.domain.DocumentRepository;
import com.kite9.k9server.domain.RevisionRepository;

public interface Command {
	
	public long getDocumentId();
	
	public Change applyCommand(Document d, DocumentRepository dr, RevisionRepository rr) throws CommandException;
	
}
