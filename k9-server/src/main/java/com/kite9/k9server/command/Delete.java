package com.kite9.k9server.command;

import org.kite9.diagram.dom.elements.ADLDocument;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.kite9.k9server.adl.holder.ADL;

public class Delete extends AbstractCommand {
		
	public Delete() {
		super();
	}
	
	private boolean cascade;
	
	public Delete(String fragmentId, String fragmentHash, boolean cascade) {
		super(fragmentId, fragmentHash);
		this.cascade = cascade;
	}

	@Override
	public ADL applyCommand(ADL in) throws CommandException {
		ensureNotNull(this, "delete", "fragmentId", fragmentId);

		ADLDocument doc = in.getAsDocument();
		
		validateFragmentHash(in);
		Element e = doc.getElementById(fragmentId);
		if (e==null) {
			// elements that can't be found may have been deleted in another step, so not an error.
			return in;
		}
		
		Node parent = e.getParentNode();
	
		if (!cascade) {
			NodeList children = e.getChildNodes();
			while (children.getLength() > 0) {
				Node c = children.item(0);
				parent.insertBefore(c, e);
			}
		}
		
		parent.removeChild(e);
		LOG.info("Processed delete of " + fragmentId);
		return in;
	}

}
