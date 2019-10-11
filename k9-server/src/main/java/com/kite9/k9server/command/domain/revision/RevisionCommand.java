package com.kite9.k9server.command.domain.revision;

import com.kite9.k9server.adl.holder.ADL;
import com.kite9.k9server.command.CommandException;
import com.kite9.k9server.command.domain.DomainCommand;
import com.kite9.k9server.domain.document.Document;

/**
 * Modifies the revision history.
 * 
 * @author robmoffat
 *
 */
public interface RevisionCommand extends DomainCommand<Document>{

	ADL applyCommand() throws CommandException;
}
