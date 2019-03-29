package com.kite9.k9server.adl.command;

import java.util.ArrayList;
import java.util.List;

import org.kite9.diagram.dom.CSSConstants;
import org.kite9.diagram.dom.elements.ReferencingKite9XMLElement;
import org.kite9.diagram.dom.elements.StyledKite9XMLElement;
import org.kite9.diagram.dom.managers.EnumValue;
import org.kite9.diagram.model.style.DiagramElementType;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class ADLReferenceHandler {

	/**
	 * This is hard-coded for now, but in the future we need to use xpaths / css attributes.
	 * somehow, this is the concern of Kite9Visualization rather than here..
	 *
	 * @deprecated Needs to use ReferencingKite9XMLElement somehow
	 */
	@Deprecated
	public static void hardcodedReferenceReplace(Element insert, List<String> refs) {
		int refNo = 0;
		NodeList nl = insert.getChildNodes();
		for (int i = 0; i < nl.getLength(); i++) {
			Node n = nl.item(i);
			
			if (n instanceof StyledKite9XMLElement) {
				EnumValue v = (EnumValue) ((StyledKite9XMLElement) n).getCSSStyleProperty(CSSConstants.ELEMENT_TYPE_PROPERTY);
				
				if (v.getTheValue() == DiagramElementType.LINK_END) {
					String ref = refs.get(refNo);
					((StyledKite9XMLElement) n).setAttribute("reference", ref);
					refNo++;
				}
			}
		}
	}
	
	/**
	 * Removes any link references that are broken.
	 */
	public static void checkReferences(Node n) {
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

	private static List<Node> copyToList(NodeList nl) {
		List<Node> out = new ArrayList<>(nl.getLength());
		for (int i = 0; i < nl.getLength(); i++) {
			out.add(nl.item(i));
		}
		return out;
	} 


}
