package com.kite9.k9server.command.xml;

import com.kite9.k9server.adl.holder.ADL;
import com.kite9.k9server.command.Command;

/**
 * ADL Commands manipulate some XML, which is passed in using setOn
 *
 */
public interface XMLCommand extends Command {

	void setOn(ADL on);
	
}
