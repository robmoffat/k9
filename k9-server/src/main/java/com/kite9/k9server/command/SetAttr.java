package com.kite9.k9server.command;

import org.kite9.diagram.dom.elements.ADLDocument;
import org.w3c.dom.Element;

import com.kite9.k9server.adl.holder.ADL;

public class SetAttr extends AbstractCommand {

	String name, value;
	
	public SetAttr() {
		super();
	}

	public SetAttr(String fragmentId, String fragmentHash, String name, String value) {
		super(fragmentId, fragmentHash);
		this.name = name;
		this.value = value;
	}

	@Override
	public void applyCommand(ADL adl) throws CommandException {
		ensureNotNull(this, "setAttr", "name", name);
		
		ADLDocument doc = adl.getAsDocument();
		validateFragmentHash(doc);
		Element e = findFragmentElement(doc);

		if (value == null) {
			e.removeAttribute(name);
		} else {
			e.setAttribute(name, value);
		}
	
		
		LOG.info("Processed setAttr of "+fragmentId);
	}
	
}
