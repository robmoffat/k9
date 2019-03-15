package com.kite9.k9server.command;

import org.apache.batik.dom.AbstractAttr;
import org.apache.batik.dom.AbstractElement;
import org.apache.batik.dom.AbstractNode;
import org.kite9.diagram.dom.elements.ADLDocument;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public abstract class AbstractLocatedCommand extends AbstractCommand {

	protected String beforefragmentId; 	// before this guy, if supplied.  otherwise, at end.
	
	public AbstractLocatedCommand() {
		super();
	}

	public AbstractLocatedCommand(String fragmentId, String fragmentHash, String beforefragmentId) {
		super(fragmentId, fragmentHash);
		this.beforefragmentId = beforefragmentId;
	}

	protected void insert(ADLDocument doc, Element e) {
		ensureNotNull(this, "insert", "fragment", fragmentId);
		
		Element into = findFragmentElement(doc);
		
		Element before = null;
		if (beforefragmentId != null) {
			before = doc.getElementById(beforefragmentId);
		}
		
		into.insertBefore(e, before);
		ensureParentElements(into, e);
	}
	
	/**
	 * For some reason, in the clone node process, batik clears the parent element setting, and this doesn't get fixed.
	 */
	private void ensureParentElements(Node parent, Node child) {
		if ((child instanceof Attr) && (((Attr)child).getOwnerElement() == null)) {
			((AbstractAttr)child).setOwnerElement((AbstractElement) parent);
		}
		
		if (child instanceof Element) {
			NodeList children = child.getChildNodes();
			for (int i = 0; i < children.getLength(); i++) {
				ensureParentElements(child, children.item(i));
			}
			
			NamedNodeMap map = child.getAttributes();
			for (int i = 0; i < map.getLength(); i++) {
				ensureParentElements(child, map.item(i));
			}
		}
	}


 	
}
