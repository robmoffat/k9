package com.kite9.k9server.command;

import org.apache.batik.anim.dom.SVGOMSVGElement;
import org.kite9.diagram.dom.elements.ADLDocument;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.kite9.k9server.adl.holder.ADL;

public class SetXML extends AbstractCommand {

	String newState;
	
	public SetXML() {
		super();
	}

	public SetXML(String fragmentId, String fragmentHash, String newState) {
		super(fragmentId, fragmentHash);
		this.newState = newState;
	}

	@Override
	public ADL applyCommand(ADL adl) throws CommandException {
		ensureNotNull(this, "setXML", "newState", newState);
		
		ADLDocument doc = adl.getAsDocument();
		validateFragmentHash(adl);
		Element e = findFragmentElement(doc);

		// replace the child elements with ids - these should stay as the original
		ADLDocument nDoc = adl.loadXMLDocument(newState, adl.getUri());
		Element n = getSingleContentElement(nDoc, this);
		replaceElementsWithIds(n, doc);
		
		// replace the old with the new
		doc.adoptNode(n);
		e.getParentNode().replaceChild(n, e);
		
		LOG.info("Processed setXML of "+fragmentId);
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
				throw new CommandException("Was expecting a single element within the svg document, but there are  "+childElementCount+" elements", c);
			}
			
			return ((SVGOMSVGElement) svg).getFirstElementChild();
			
		} else {
			throw new CommandException("Was expecting SVG document: "+svg.getClass(), c);
		}
	}

}
