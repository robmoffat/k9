package com.kite9.k9server.command;

import org.kite9.diagram.dom.elements.ADLDocument;
import org.kite9.diagram.dom.elements.StyledKite9XMLElement;
import org.w3c.dom.Element;
import org.w3c.dom.css.CSSStyleDeclaration;

import com.kite9.k9server.adl.holder.ADL;

public class SetStyle extends AbstractCommand {

	String name, value;
	
	public SetStyle() {
		super();
	}

	public SetStyle(String fragmentId, String fragmentHash, String name, String value) {
		super(fragmentId, fragmentHash);
		this.name = name;
		this.value = value;
	}

	@Override
	public ADL applyCommand(ADL adl) throws CommandException {
		ensureNotNull(this, "setStyle", "name", name);
		
		ADLDocument doc = adl.getAsDocument();
		adl.getTranscoder().ensureCSSEngine(doc);
		validateFragmentHash(adl);
		Element e = findFragmentElement(doc);
		if (e instanceof StyledKite9XMLElement) {
			CSSStyleDeclaration sd = ((StyledKite9XMLElement) e).getStyle();
			if (value == null) {
				sd.removeProperty(name);
			} else {
				sd.setProperty(name, value, "");
			}
		}

		LOG.info("Processed setStyle of "+fragmentId);
		return adl;
	}
	
	
	
}
