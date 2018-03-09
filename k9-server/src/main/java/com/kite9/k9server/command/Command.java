package com.kite9.k9server.command;

import com.kite9.k9server.adl.holder.ADL;
import com.kite9.k9server.domain.document.Document;
import com.kite9.k9server.domain.user.User;

public interface Command {
	
	public Document getDocument();
	
	public User getAuthor();
		
	public ADL applyCommand(ADL adl) throws CommandException;
	
}
