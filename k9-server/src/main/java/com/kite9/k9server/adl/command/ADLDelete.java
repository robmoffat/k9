package com.kite9.k9server.adl.command;

import java.util.ArrayList;
import java.util.List;

import org.kite9.diagram.dom.CSSConstants;
import org.kite9.diagram.dom.elements.ADLDocument;
import org.kite9.diagram.dom.elements.ReferencingKite9XMLElement;
import org.kite9.diagram.dom.elements.StyledKite9XMLElement;
import org.kite9.diagram.model.style.DiagramElementType;
import org.kite9.framework.common.LinkReferenceException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

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
		checkReferences(doc);
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

	/**
	 * Removes any link references that are broken.
	 */
	private void checkReferences(Node n) {
		if (n instanceof ReferencingKite9XMLElement) {
			if (((ReferencingKite9XMLElement) n).getType() == DiagramElementType.LINK) {
				
				String fromId = ((ReferencingKite9XMLElement) n).getIDReference(CSSConstants.LINK_FROM_XPATH);
				String toId = ((ReferencingKite9XMLElement) n).getIDReference(CSSConstants.LINK_TO_XPATH);
				
				if (n.getOwnerDocument().getElementById(fromId) == null) {
					n.getParentNode().removeChild(n);
				} else if (n.getOwnerDocument().getElementById(toId) == null) {
					n.getParentNode().removeChild(n);
				}
			}
		}
		
		if (n instanceof Element) {
			NodeList nl = n.getChildNodes();
			List<Node> copy = copyToList(nl);
			for (Node c : copy) {
				checkReferences(c);
			}
		}
		
		if (n instanceof Document) {
			checkReferences(((Document) n).getDocumentElement());
		}
	}

	private List<Node> copyToList(NodeList nl) {
		List<Node> out = new ArrayList<>(nl.getLength());
		for (int i = 0; i < nl.getLength(); i++) {
			out.add(nl.item(i));
		}
		return out;
	} 

}
