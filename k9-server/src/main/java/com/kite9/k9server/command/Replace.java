package com.kite9.k9server.command;

import java.util.Arrays;
import java.util.List;

import org.kite9.diagram.dom.elements.ADLDocument;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.kite9.k9server.adl.holder.ADL;

public class Replace extends AbstractCommand {

	String fromUri;
	boolean replaceContents = false;
	List<String> keptAttributes = Arrays.asList("id");	// just keep ID by default.
	
	
	public Replace() {
		super();
	}

	public Replace(String fragmentId, String fragmentHash, String fromUri, boolean replaceContents, List<String> keptAttributes) {
		super(fragmentId, fragmentHash);
		this.replaceContents = replaceContents;
		this.keptAttributes = keptAttributes;
		this.fromUri = fromUri;
	}

	@Override
	public ADL applyCommand(ADL adl) throws CommandException {
		ensureNotNull(this, "setXML", "fromuri", fromUri);
		
		ADLDocument doc = adl.getAsDocument();
		validateFragmentHash(adl);
		Element e = findFragmentElement(doc);
		Element n = getForeignElementCopy(doc, adl.getUri(), fromUri, replaceContents);
		

		if (!replaceContents) {
			moveContents(e, n);
		}
		
		handleKeptAttributes(e, n);
		
		// replace the old with the new
		doc.adoptNode(n);
		e.getParentNode().replaceChild(n, e);
		
		LOG.info("Processed setXML of "+fragmentId);
		return adl;
	}
	
	private void handleKeptAttributes(Element e, Element n) {
		keptAttributes.forEach(a -> {
			if (e.hasAttribute(a)) {
				n.setAttribute(a, e.getAttribute(a));
			}
		});
	}

	private void moveContents(Element from, Element to) {
		NodeList toNodes = to.getChildNodes();
		while (toNodes.getLength() > 0) {
			to.removeChild(toNodes.item(0));
		}
		
		NodeList fromNodes = from.getChildNodes();
		while (fromNodes.getLength() > 0) {
			to.appendChild(fromNodes.item(0));
		}
	}
	
}
