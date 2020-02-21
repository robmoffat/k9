package com.kite9.k9server.command;

import com.kite9.k9server.adl.holder.ADL;

/**
 * ADL Commands manipulate some XML, which is passed in using setOn.
 * 
 * XMLCommands will result in a new revision being created on the xml they are changing.
 *
 */
public interface XMLCommand extends Command {

	void setOn(ADL on);
	
	/**
	 * XML commands can optionally provide the ADL as base64 encoded, if there
	 * is no context url to load
	 */
	String getBase64EncodedState();

	@Override
	ADL applyCommand() throws CommandException;
	
	
	
}
