package com.kite9.k9server.command;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kite9.framework.xml.ADLDocument;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.kite9.k9server.adl.holder.ADL;
import com.kite9.k9server.adl.holder.ADLImpl;

public class Step {
	
	private static final Log LOG = LogFactory.getLog(Step.class);

	StepType type;
	
	String afterNodeId;
	String insideNodeId;
	String nodeId;
	boolean deep;
	
	String existingState;
	String newState;
	
	public Step(StepType type, String afterNodeId, String insideNodeId, String nodeId, String existingState, String newState) {
		super();
		this.type = type;
		this.afterNodeId = afterNodeId;
		this.insideNodeId = insideNodeId;
		this.nodeId = nodeId;
		this.existingState = existingState;
		this.newState = newState;
	}

	public ADL apply(Command c, ADL adl) throws CommandException {
		switch (this.type) {
		case DELETE:
			return delete(c, adl, this.nodeId);
		case MODIFY:
			return modify(c, adl, this.nodeId, this.existingState, this.newState);
		case MOVE:
			return move(c, adl, this.nodeId, this.afterNodeId, this.insideNodeId);
		case CREATE:
			return create(c, adl, this.newState, this.insideNodeId, this.afterNodeId);
		case CREATE_DOC:
		default:
			return createDoc(c, newState, adl);
		}
	}
	
	public static ADL create(Command c, ADL adl, String ns, String inside, String after) {
		// TODO Auto-generated method stub
		return null;
	}

	public static ADL createDoc(Command c, String newState2, ADL adl) throws CommandException {
		if (adl != null) {
			throw new CommandException("createdoc requires empty document", c);
		}
		return new ADLImpl(newState2,"someuri");
	}

	private ADL move(Command c, ADL adl, String nodeId2, String afterNodeId2, String insideNodeId2) {
		// TODO Auto-generated method stub
		
	}

	public static void ensureNotNull(Command c, String operation, String field, Object n) throws CommandException {
		if (n == null) {
			throw new CommandException(operation+" requires "+field+" to be set", c);
		}
	}
	
	public static ADL delete(Command c, ADL adl, String nodeId) throws CommandException {
		ensureNotNull(c, "delete", "nodeId", nodeId);
		
		ADLDocument doc = adl.getAsDocument();
		Element e = doc.getElementById(nodeId);
		Node parent = e.getParentNode();
		parent.removeChild(e);
		
		LOG.info("Processed delete of "+nodeId);
	}
	
	public static ADL modify(Command c, ADL adl, String nodeId, String oldState, String newState) throws CommandException {
		ensureNotNull(c, "modify", "nodeId", nodeId);
		ensureNotNull(c, "modify", "oldState", oldState);
		ensureNotNull(c, "modify", "newState", newState);
		
		
		ADLDocument doc = adl.getAsDocument();
		Element e = doc.getElementById(nodeId);
		String serialized = adl.getAsXMLString(e);
		
		if ()
		
	}
}
