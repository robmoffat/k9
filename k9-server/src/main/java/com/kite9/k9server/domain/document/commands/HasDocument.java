package com.kite9.k9server.domain.document.commands;

import com.kite9.k9server.domain.document.DocumentRepository;
import com.kite9.k9server.domain.revision.Revision;

public interface HasDocument {

	public void setCommandContext(DocumentRepository repo, Revision current, String url);
}
