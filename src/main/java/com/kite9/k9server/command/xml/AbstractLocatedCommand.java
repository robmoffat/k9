package com.kite9.k9server.command.xml;

import org.kite9.diagram.dom.elements.ADLDocument;
import org.w3c.dom.Element;

import com.kite9.k9server.adl.holder.ADL;

public abstract class AbstractLocatedCommand extends AbstractADLCommand {

	protected String beforeFragmentId; 	// before this guy, if supplied.  otherwise, at end.
	
	public AbstractLocatedCommand() {
		super();
	}

	public AbstractLocatedCommand(String fragmentId, ADL on, String beforefragmentId) {
		super(on, fragmentId);
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
