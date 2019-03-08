package com.kite9.k9server.domain.document.commands;

import com.kite9.k9server.command.AbstractCommand;
import com.kite9.k9server.domain.document.DocumentRepository;
import com.kite9.k9server.domain.revision.Revision;

public abstract class AbstractDocumentCommand extends AbstractCommand implements HasDocument {

	protected DocumentRepository repo;
	protected Revision currentRevision;
	protected String url;
	
	public AbstractDocumentCommand() {
		super();
	}

	public AbstractDocumentCommand(String fragmentId, String fragmentHash) {
		super(fragmentId, fragmentHash);
	}
	
	@Override
	public void setCommandContext(DocumentRepository repo, Revision current, String url) {
		this.repo = repo;
		this.currentRevision = current;
		this.url = url;
	} 

}
