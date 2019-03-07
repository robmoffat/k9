package com.kite9.k9server.command;

import org.kite9.diagram.dom.elements.ADLDocument;
import org.w3c.dom.Element;

import com.kite9.k9server.adl.holder.ADL;

public class Move extends AbstractLocatedCommand {

	protected String moveId;  	// guy we are moving
	
	public Move() {
		super();
	}

	public Move(String fragmentId, String fragmentHash, String beforefragmentId, String moveId) {
		super(fragmentId, fragmentHash, beforefragmentId);
		this.moveId = moveId;
	}

	@Override
	public ADL applyCommand(ADL adl) throws CommandException {
		ensureNotNull(this, "move", "moveId", moveId);
		
		ADLDocument doc = adl.getAsDocument();
		validateFragmentHash(adl);
		Element e = doc.getElementById(moveId);		
		if (e == null) {
			throw new CommandException("No element for moveId: "+fragmentId, this);
		}

		insert(doc, e);

		LOG.info("Processed move of "+fragmentId);
		return adl;
	}

}
