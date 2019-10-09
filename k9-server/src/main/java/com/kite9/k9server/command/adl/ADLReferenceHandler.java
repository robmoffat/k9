package com.kite9.k9server.command.adl;

import java.util.ArrayList;
import java.util.List;

import org.kite9.diagram.dom.CSSConstants;
import org.kite9.diagram.dom.elements.ReferencingKite9XMLElement;
import org.kite9.diagram.model.style.DiagramElementType;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class ADLReferenceHandler {

	/**
	 * Removes any link references that are broken, and orphaned terminators
	 */
	public static void checkConsistency(Node n) {
	
		if (isType(n, DiagramElementType.LINK)) {
			
			String fromId = ((ReferencingKite9XMLElement) n).getIDReference(CSSConstants.LINK_FROM_XPATH);
			String toId = ((ReferencingKite9XMLElement) n).getIDReference(CSSConstants.LINK_TO_XPATH);
			
			if (n.getOwnerDocument().getElementById(fromId) == null) {
				n.getParentNode().removeChild(n);
			} else if (n.getOwnerDocument().getElementById(toId) == null) {
				n.getParentNode().removeChild(n);
			}
		}
		
		if (isType(n, DiagramElementType.LINK_END)) {
			if (!isType(n.getParentNode(), DiagramElementType.LINK)) {
				n.getParentNode().removeChild(n);
			}
		}
		
		
		if (n instanceof Element) {
			NodeList nl = n.getChildNodes();
			List<Node> copy = copyToList(nl);
			for (Node c : copy) {
				checkConsistency(c);
			}
		}
		
		if (n instanceof Document) {
			checkConsistency(((Document) n).getDocumentElement());
		}
	}

	protected static boolean isType(Node n, DiagramElementType t) {
		if (n instanceof ReferencingKite9XMLElement) {
			return ((ReferencingKite9XMLElement) n).getType() == t;
		} else {
			return false;
		}
	}

	private static List<Node> copyToList(NodeList nl) {
		List<Node> out = new ArrayList<>(nl.getLength());
		for (int i = 0; i < nl.getLength(); i++) {
			out.add(nl.item(i));
		}
		return out;
	} 


}
