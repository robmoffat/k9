package com.kite9.k9server.command.xml;

import org.kite9.diagram.dom.elements.ADLDocument;
import org.springframework.http.HttpStatus;
import org.w3c.dom.Element;

import com.kite9.k9server.adl.holder.ADL;
import com.kite9.k9server.command.CommandException;

public class Move extends AbstractLocatedCommand {

	protected String moveId;  	// guy we are moving
	
	public Move() {
		super();
	}

	public Move(String fragmentId, ADL on, String beforefragmentId, String moveId) {
		super(fragmentId, on, beforefragmentId);
		this.moveId = moveId;
	}

	@Override
	public ADL applyCommand() throws CommandException {
		ensureNotNull(this, "move", "moveId", moveId);
		
		ADLDocument doc = adl.getAsADLDocument();
		Element e = doc.getElementById(moveId);		
		if (e == null) {
			throw new CommandException(HttpStatus.NOT_FOUND, "No element for moveId: "+fragmentId, this);
		}

		insert(doc, e);

		LOG.info("Processed move of "+moveId+ " in "+ fragmentId);
		return adl;
	}

}
