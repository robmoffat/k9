package com.kite9.k9server.command.xml;

import org.kite9.diagram.dom.elements.ADLDocument;
import org.w3c.dom.Element;

import com.kite9.k9server.adl.holder.ADL;
import com.kite9.k9server.command.CommandException;

public class SetText extends AbstractADLCommand {
	
	public String newText;
	
	public SetText() {
		super();
	}

	public SetText(String fragmentId, ADL on, String newText) {
		super(on, fragmentId);
		this.newText = newText;
	}

	@Override
	public ADL applyCommand() throws CommandException {
		ensureNotNull(this, "edit", "fragment", fragmentId);
		ensureNotNull(this, "edit", "newText", newText);
		ADLDocument doc = adl.getAsDocument();
		Element e = doc.getElementById(fragmentId);
		e.setTextContent(newText);
		LOG.info("Processed edit of "+fragmentId);
		return adl;
	}


}
