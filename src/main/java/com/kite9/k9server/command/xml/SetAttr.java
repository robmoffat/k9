package com.kite9.k9server.command.xml;

import org.kite9.diagram.dom.elements.ADLDocument;
import org.w3c.dom.Element;

import com.kite9.k9server.adl.holder.ADL;
import com.kite9.k9server.command.CommandException;

public class SetAttr extends AbstractADLCommand {

	String name, value;
	
	public SetAttr() {
		super();
	}

	public SetAttr(String fragmentId, ADL on, String name, String value) {
		super(on, fragmentId);
		this.name = name;
		this.value = value;
	}

	@Override
	public ADL applyCommand() throws CommandException {
		ensureNotNull(this, "setAttr", "name", name);
		
		ADLDocument doc = adl.getAsADLDocument();
		Element e = findFragmentElement(doc);

		if (value == null) {
			e.removeAttribute(name);
		} else {
			e.setAttribute(name, value);
		}
	
		
		LOG.info("Processed setAttr of "+fragmentId);
		return adl;
	}
	
}
