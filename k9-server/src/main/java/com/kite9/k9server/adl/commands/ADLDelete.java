package com.kite9.k9server.adl.commands;

import java.util.ArrayList;
import java.util.List;

import org.kite9.diagram.dom.elements.ADLDocument;
import org.kite9.diagram.dom.elements.Kite9XMLElement;
import org.kite9.diagram.model.Connection;
import org.kite9.diagram.model.DiagramElement;
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
		ADL out = super.applyCommand(in);
		ADLDocument doc = out.getAsDocument();
		out.getTranscoder().ensureCSSEngine(doc);
		checkReferences(doc);
		return out;
	}

	/**
	 * Removes any link references that are broken.
	 */
	private void checkReferences(Node n) {
		if (n instanceof Kite9XMLElement) {
			try {
				DiagramElement de = ((Kite9XMLElement) n).getDiagramElement();
				if (de instanceof Connection) {
					((Connection) de).getFrom();
					((Connection) de).getTo();
				}
			} catch (LinkReferenceException e) {
				n.getParentNode().removeChild(n);
				LOG.debug("Removed orphan link: "+((Element)n).getAttribute("id"));
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
