package com.kite9.k9server.command;

import org.apache.batik.anim.dom.SVGOMSVGElement;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kite9.framework.xml.ADLDocument;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xmlunit.builder.DiffBuilder;
import org.xmlunit.diff.ComparisonControllers;
import org.xmlunit.diff.Diff;

import com.kite9.k9server.adl.holder.ADL;
import com.kite9.k9server.adl.holder.ADLImpl;

public class Step {
	
	private static final Log LOG = LogFactory.getLog(Step.class);

	StepType type;
	
	String beforeNodeId;
	String insideNodeId;
	String nodeId;
	boolean deep;
	
	String existingState;
	String newState;
	
	public Step(StepType type, String beforeNodeId, String insideNodeId, String nodeId, String existingState, String newState) {
		super();
		this.type = type;
		this.beforeNodeId = beforeNodeId;
		this.insideNodeId = insideNodeId;
		this.nodeId = nodeId;
		this.existingState = existingState;
		this.newState = newState;
	}

	public ADL apply(Command c, ADL adl) throws CommandException {
		switch (this.type) {
		case DELETE:
			return delete(c, adl, this.nodeId, this.existingState);
		case MODIFY:
			return modify(c, adl, this.nodeId, this.existingState, this.newState);
		case MOVE:
			return move(c, adl, this.nodeId, this.beforeNodeId, this.insideNodeId, this.existingState);
		case CREATE_DOC:
		default:
			return createDoc(c, newState, adl);
		}
	}

	public static ADL createDoc(Command c, String newState2, ADL adl) throws CommandException {
		if (adl != null) {
			throw new CommandException("createdoc requires empty document", c);
		}
		return new ADLImpl(newState2,"someuri");
	}

	private ADL move(Command c, ADL adl, String nodeId, String beforeNodeId, String insideNodeId, String oldState) throws CommandException {
		ensureNotNull(c, "move", "nodeId", nodeId);
		ensureNotNull(c, "move", "oldState", oldState);
		ensureNotNull(c, "move", "insideNodeId", insideNodeId);
		
		ADLDocument doc = adl.getAsDocument();
		Element e = doc.getElementById(nodeId);

		ADLDocument oDoc = adl.loadXMLDocument(oldState, adl.getUri());
		Element o = getSingleContentElement(oDoc, c);

		Element inside = doc.getElementById(insideNodeId);		
		if (inside == null) {
			throw new CommandException("No element for id: "+insideNodeId, c);
		}
		
		compareElements(c, insideNodeId, inside, o);

		
		Element before = null;
		if (beforeNodeId != null) {
			before = doc.getElementById(beforeNodeId);
			if (before == null) {
				throw new CommandException("No element for id: "+beforeNodeId, c);
			}
		}

		inside.insertBefore(e, before);
		LOG.info("Processed move of "+nodeId);
		
		return adl;		
	}

	public static void ensureNotNull(Command c, String operation, String field, Object n) throws CommandException {
		if (n == null) {
			throw new CommandException(operation+" requires "+field+" to be set", c);
		}
	}
	
	public static ADL delete(Command c, ADL adl, String nodeId, String oldState) throws CommandException {
		ensureNotNull(c, "delete", "nodeId", nodeId);
		ensureNotNull(c, "delete", "oldState", oldState);
		
		ADLDocument doc = adl.getAsDocument();
		Element e = doc.getElementById(nodeId);
		
		ADLDocument oDoc = adl.loadXMLDocument(oldState, adl.getUri());
		Element o = getSingleContentElement(oDoc, c);
		compareElements(c, nodeId, e, o);
		
		Node parent = e.getParentNode();
		parent.removeChild(e);
		
		LOG.info("Processed delete of "+nodeId);
		return adl;
	}
	
	public static ADL modify(Command c, ADL adl, String nodeId, String oldState, String newState) throws CommandException {
		ensureNotNull(c, "modify", "nodeId", nodeId);
		ensureNotNull(c, "modify", "oldState", oldState);
		ensureNotNull(c, "modify", "newState", newState);
		
		ADLDocument doc = adl.getAsDocument();
		Element e = doc.getElementById(nodeId);

		ADLDocument oDoc = adl.loadXMLDocument(oldState, adl.getUri());
		Element o = getSingleContentElement(oDoc, c);
		
		compareElements(c, nodeId, e, o);
		
		// replace the child elements with ids - these should stay as the original
		ADLDocument nDoc = adl.loadXMLDocument(newState, adl.getUri());
		Element n = getSingleContentElement(nDoc, c);
		replaceElementsWithIds(n, doc);
		
		// replace the old with the new
		doc.adoptNode(n);
		e.getParentNode().replaceChild(n, e);
		
		LOG.info("Processed modify of "+nodeId);

		
		return adl;
	}

	private static Element getSingleContentElement(ADLDocument d, Command c) throws CommandException {
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

	private static void replaceElementsWithIds(Element n, ADLDocument e) {
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

	public static void compareElements(Command c, String nodeId, Element e, Element o) throws CommandException {
		Diff diff = DiffBuilder.compare(o).withTest(e)
			.ignoreWhitespace()
			.ignoreComments()
			.withComparisonController(ComparisonControllers.StopWhenDifferent)
			.withNodeFilter(n -> {
				// if the node has a parent with an ID, don't bother to compare it
				Node parent = n.getParentNode();
				if ((parent instanceof Element) && (((Element)parent).hasAttribute("id"))) {
					
					String id = ((Element) parent).getAttribute("id");
					if (!id.equals(nodeId)) {
						return false;
					} 
				} 
				
				return true;
			}).build();
		
		if (diff.hasDifferences()) {
			throw new CommandException("Differences in expected/actual state"+diff.getDifferences(), c);
		}
	}
}
