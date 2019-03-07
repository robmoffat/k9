package com.kite9.k9server.command;

import org.kite9.diagram.dom.elements.ADLDocument;
import org.w3c.dom.Element;

import com.kite9.k9server.adl.holder.ADL;

public class SetText extends AbstractCommand {
	
	protected String newText;
	
	public SetText() {
		super();
	}

	public SetText(String fragmentId, String fragmentHash, String newText) {
		super(fragmentId, fragmentHash);
		this.newText = newText;
	}

	@Override
	public ADL applyCommand(ADL adl) throws CommandException {
		ensureNotNull(this, "edit", "fragment", fragmentId);
		ensureNotNull(this, "edit", "newText", newText);
		ADLDocument doc = adl.getAsDocument();
		validateFragmentHash(adl);
		Element e = doc.getElementById(fragmentId);
		e.setTextContent(newText);
		LOG.info("Processed edit of "+fragmentId);
		return adl;
	}


}
