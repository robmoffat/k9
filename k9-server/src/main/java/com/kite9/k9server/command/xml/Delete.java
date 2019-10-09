package com.kite9.k9server.command.xml;

import org.kite9.diagram.dom.elements.ADLDocument;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.kite9.k9server.adl.holder.ADL;
import com.kite9.k9server.command.CommandException;

public class Delete extends AbstractADLCommand {
		
	public Delete() {
		super();
	}
	
	protected boolean cascade;
	
	public Delete(String fragmentId, ADL on, boolean cascade) {
		super(on, fragmentId);
		this.cascade = cascade;
	}

	@Override
	public ADL applyCommand() throws CommandException {
		ensureNotNull(this, "delete", "fragmentId", fragmentId);

		ADLDocument doc = adl.getAsDocument();
		
		Element e = doc.getElementById(fragmentId);
		if (e==null) {
			// elements that can't be found may have been deleted in another step, so not an error.
			return adl;
		}
		
		performDelete(e);
		LOG.info("Processed delete of " + fragmentId);
		return adl;
	}

	protected void performDelete(Element e) {
		Node parent = e.getParentNode();
	
		if (!cascade) {
			NodeList children = e.getChildNodes();
			while (children.getLength() > 0) {
				Node c = children.item(0);
				if (c instanceof Element) {
					parent.insertBefore(c, e);
				} else {
					e.removeChild(c);
				}
			}
		}
		
		parent.removeChild(e);
	}

}
