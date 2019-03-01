package com.kite9.k9server.command;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kite9.diagram.dom.elements.ADLDocument;
import org.w3c.dom.Element;

public abstract class AbstractCommand implements Command {
	
	protected String fragmentId;
	
	public AbstractCommand() {
		super();
	}

	public AbstractCommand(String fragmentId) {
		super();
		this.fragmentId = fragmentId;
	}

	public static final Log LOG = LogFactory.getLog(Command.class);
	
	public static void ensureNotNull(Command c, String operation, String field, Object n) throws CommandException {
		if (n == null) {
			throw new CommandException(operation+" requires "+field+" to be set", c);
		}
	}
	
	protected Element findFragmentElement(ADLDocument doc) {
		Element into = doc.getElementById(fragmentId);
		if (into == null) {
			throw new CommandException("Could not locate: "+fragmentId, this);
		}
		return into;
	}
}
