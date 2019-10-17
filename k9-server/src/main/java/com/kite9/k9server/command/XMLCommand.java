package com.kite9.k9server.command;

import com.kite9.k9server.adl.holder.ADL;

/**
 * ADL Commands manipulate some XML, which is passed in using setOn
 *
 */
public interface XMLCommand extends Command {

	void setOn(ADL on);

	@Override
	ADL applyCommand() throws CommandException;
	
	
	
}
