package com.kite9.k9server.command.xml;

import org.apache.batik.anim.dom.SVGOMSVGElement;
import org.kite9.diagram.dom.elements.ADLDocument;
import org.springframework.http.HttpStatus;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.kite9.k9server.adl.holder.ADL;
import com.kite9.k9server.command.Command;
import com.kite9.k9server.command.CommandException;

public class AppendXML extends AbstractLocatedCommand {

	String newState;
	
	public AppendXML() {
		super();
	}

	public AppendXML(String fragmentId, ADL on, String beforeFragmentId, String newState) {
		super(fragmentId, on, beforeFragmentId);
		this.newState = newState;
	}

	@Override
	public ADL applyCommand() throws CommandException {
		ensureNotNull(this, "appendXML", "newState", newState);
		
		ADLDocument doc = adl.getAsADLDocument();

		// create the new child element
		ADLDocument nDoc = adl.parseDocument(newState, adl.getUri());
		Element n = getSingleContentElement(nDoc, this);
		replaceElementsWithIds(n, doc);
		
		// insert the new element
		doc.adoptNode(n);
		insert(doc, n);
		
		LOG.info("Processed appendXML of "+fragmentId);
		return adl;
	}
	
	private void replaceElementsWithIds(Element n, ADLDocument e) {
		NodeList nodes = n.getChildNodes(); 
		for (int i = 0; i < nodes.getLength(); i++) {
			Node child = nodes.item(i);
			if (child instanceof Element) {
				if (((Element) child).hasAttribute("id")) {
					String id = ((Element) child).getAttribute("id");
					Element replaceWith = e.getElementById(id);
					if (replaceWith != null) {
						n.getOwnerDocument().adoptNode(replaceWith);
						n.replaceChild(replaceWith, child);
						continue;
					}
				}
				
				replaceElementsWithIds((Element) child, e);
			} 
		}
	}
	
	
	private Element getSingleContentElement(ADLDocument d, Command c) throws CommandException {
		Element svg = d.getDocumentElement();
		if (svg instanceof SVGOMSVGElement) {
			int childElementCount = ((SVGOMSVGElement) svg).getChildElementCount();
			if (childElementCount != 1) {
				throw new CommandException(HttpStatus.CONFLICT, "Was expecting a single element within the svg document, but there are  "+childElementCount+" elements", c);
			}
			
			return ((SVGOMSVGElement) svg).getFirstElementChild();
			
		} else {
			throw new CommandException(HttpStatus.CONFLICT, "Was expecting SVG document: "+svg.getClass(), c);
		}
	}

}
