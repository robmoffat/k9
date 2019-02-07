package com.kite9.k9server.command;

import com.kite9.k9server.adl.holder.ADL;

public interface Command {
	
	public ADL getInput();
				
	public ADL applyCommand() throws CommandException;
	
}
