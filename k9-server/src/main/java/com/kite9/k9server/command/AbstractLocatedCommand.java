package com.kite9.k9server.command;

import org.kite9.diagram.dom.elements.ADLDocument;
import org.w3c.dom.Element;

public abstract class AbstractLocatedCommand extends AbstractCommand {

	protected String beforefragmentId; 	// before this guy, if supplied.  otherwise, at end.
	
	public AbstractLocatedCommand() {
		super();
	}

	public AbstractLocatedCommand(String fragmentId, String beforefragmentId) {
		super(fragmentId);
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
	}
 	
}
