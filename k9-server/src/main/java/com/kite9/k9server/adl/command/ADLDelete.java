package com.kite9.k9server.adl.command;

import org.kite9.diagram.dom.elements.ADLDocument;
import org.kite9.diagram.dom.elements.StyledKite9XMLElement;
import org.kite9.diagram.model.style.DiagramElementType;
import org.w3c.dom.Element;

import com.kite9.k9server.adl.holder.ADL;
import com.kite9.k9server.command.CommandException;
import com.kite9.k9server.command.Delete;

/**
 * Adds extra behaviour to ensure the consistency of the diagram.
 */
public class ADLDelete extends Delete {
		
	public ADLDelete() {
		super();
	}
		
	public ADLDelete(String fragmentId, String fragmentHash, boolean cascade) {
		super(fragmentId, fragmentHash, cascade);
	}

	@Override
	public ADL applyCommand(ADL in) throws CommandException {
		ADLDocument doc = in.getAsDocument();
		in.getTranscoder().ensureCSSEngine(doc);
		
		if (isDiagramElement(doc)) {
			return in;	// you can't delete the diagram
		}
		
		ADL out = super.applyCommand(in);
		ADLReferenceHandler.checkConsistency(doc);
		return out;
	}

	private boolean isDiagramElement(ADLDocument doc) {
		Element toDelete = doc.getElementById(fragmentId);
		if (toDelete instanceof StyledKite9XMLElement) {
			return (((StyledKite9XMLElement) toDelete).getType() == DiagramElementType.DIAGRAM);
		} else {
			return false;
		}
	}

	
}
