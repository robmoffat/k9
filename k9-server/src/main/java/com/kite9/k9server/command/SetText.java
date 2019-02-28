package com.kite9.k9server.command;

import org.kite9.diagram.dom.elements.ADLDocument;
import org.w3c.dom.Element;

import com.kite9.k9server.adl.holder.ADL;

public class SetText extends AbstractCommand {
	
	protected String newText;
	
	@Override
	public void applyCommand(ADL adl) throws CommandException {
		ensureNotNull(this, "edit", "fragment", fragmentId);
		ensureNotNull(this, "edit", "newText", newText);
		ADLDocument doc = adl.getAsDocument();
		Element e = doc.getElementById(fragmentId);
		e.setTextContent(newText);
		LOG.info("Processed edit of "+fragmentId);
	}


}
