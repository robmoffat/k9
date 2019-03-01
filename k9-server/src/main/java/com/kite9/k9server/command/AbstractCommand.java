package com.kite9.k9server.command;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kite9.diagram.dom.elements.ADLDocument;
import org.w3c.dom.Element;

import com.kite9.k9server.security.Hash;

public abstract class AbstractCommand implements Command {
	
	protected String fragmentId;
	protected String fragmentHash;
	
	public AbstractCommand() {
		super();
	}

	public AbstractCommand(String fragmentId, String fragmentHash) {
		super();
		this.fragmentId = fragmentId;
		this.fragmentHash = fragmentHash;
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
	
	protected void validateFragmentHash(ADLDocument doc) {
		Element e = doc.getElementById(fragmentId);
		if (e == null) {
			throw new CommandException("Could not locate: "+fragmentId, this);
		}
		String computed = Hash.generateHash(e);
		
		if (!computed.equals(fragmentHash)) {
			throw new CommandException("Fragment hash doesn't match: "+computed, this);
		}
	}
}
