package com.kite9.k9server.command.xml;

import java.util.Arrays;
import java.util.List;

import org.kite9.diagram.dom.elements.ADLDocument;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.kite9.k9server.adl.holder.ADL;
import com.kite9.k9server.command.CommandException;

public class Replace extends AbstractADLCommand {

	public enum Approach { DEEP, SHALLOW, ATTRIBUTES }
	
	String uriStr;
	Approach approach = Approach.DEEP;
	List<String> keptAttributes = Arrays.asList("id");	// just keep ID by default.
	
	
	public Replace() {
		super();
	}

	public Replace(String fragmentId, ADL on, String fromUri, Approach approach, List<String> keptAttributes) {
		super(on, fragmentId);
		this.approach = approach;
		this.keptAttributes = keptAttributes;
		this.uriStr = fromUri;
	}

	@Override
	public ADL applyCommand() throws CommandException {
		ensureNotNull(this, "Replace", "fromuri", uriStr);
		
		ADLDocument doc = adl.getAsDocument();
		Element e = findFragmentElement(doc);
		Element n = getForeignElementCopy(doc, adl.getUri(), uriStr, approach == Approach.DEEP, adl);
		
		if (approach == Approach.ATTRIBUTES) {
			// here, we keep the original, 
			// moving attributes from n -> e where they don't match the exclusion
			copyAttributes(n, e, false);
		} else {
			// this is a bit complex, but retains the accuracy of the id-element map in the doc.
			doc.adoptNode(n);
			replaceIds(n);
			e.getParentNode().insertBefore(n, e);
			ensureParentElements(e.getParentNode(), n);
			copyAttributes(e, n, true);
			
			if (approach == Approach.SHALLOW) {
				moveContents(e, n);
			}
			
			e.getParentNode().removeChild(e);
		}
		
		LOG.info("Processed Replace of "+fragmentId);
		return adl;
	}
	
	private void copyAttributes(Element from, Element to, boolean matching) {
		for (int i = 0; i < from.getAttributes().getLength(); i++) {
			Attr item = (Attr) from.getAttributes().item(i);
			if (matching == (keptAttributes.contains(item.getNodeName()))) {
				to.setAttribute(item.getNodeName(), item.getNodeValue());
			}
		}
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
