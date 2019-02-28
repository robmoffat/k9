package com.kite9.k9server.command;

import org.kite9.diagram.dom.elements.ADLDocument;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.kite9.k9server.adl.holder.ADL;

public class Delete extends AbstractCommand {

	@Override
	public void applyCommand(ADL in) throws CommandException {
		ensureNotNull(this, "delete", "fragmentId", fragmentId);

		ADLDocument doc = in.getAsDocument();
		Element e = doc.getElementById(fragmentId);
		if (e==null) {
			throw new CommandException("Couldn't find fragment: "+fragmentId, this);
		}
		
		Node parent = e.getParentNode();
		parent.removeChild(e);

		LOG.info("Processed delete of " + fragmentId);
	}

}
