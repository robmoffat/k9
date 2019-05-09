package com.kite9.k9server.command;

import org.kite9.diagram.dom.elements.ADLDocument;
import org.w3c.dom.Element;

public abstract class AbstractLocatedCommand extends AbstractCommand {

	protected String beforeFragmentId; 	// before this guy, if supplied.  otherwise, at end.
	
	public AbstractLocatedCommand() {
		super();
	}

	public AbstractLocatedCommand(String fragmentId, String fragmentHash, String beforefragmentId) {
		super(fragmentId, fragmentHash);
		this.beforeFragmentId = beforefragmentId;
	}

	protected void insert(ADLDocument doc, Element e) {
		
		Element into = null;
		Element before = null;

		if (fragmentId != null) {
			into = findFragmentElement(doc);
		}
		
		if (beforeFragmentId != null) {
			before = doc.getElementById(beforeFragmentId);
			
			if ((into == null) && (before != null)) {
				into = (Element) before.getParentNode();
			}
		}
		
		ensureNotNull(this, "insert", "into", into);

		
		into.insertBefore(e, before);
		ensureParentElements(into, e);
	}


 	
}
