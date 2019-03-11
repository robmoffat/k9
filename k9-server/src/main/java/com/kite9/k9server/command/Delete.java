package com.kite9.k9server.command;

import org.kite9.diagram.dom.CSSConstants;
import org.kite9.diagram.dom.elements.ADLDocument;
import org.kite9.diagram.dom.elements.StyledKite9SVGElement;
import org.kite9.diagram.dom.managers.EnumValue;
import org.kite9.diagram.model.style.DiagramElementType;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.kite9.k9server.adl.holder.ADL;

public class Delete extends AbstractCommand {
	
	enum DeleteType { CASCADE, SINGLE, NONE };
	
	public Delete() {
		super();
	}
	
	public Delete(String fragmentId, String fragmentHash) {
		super(fragmentId, fragmentHash);
	}

	@Override
	public ADL applyCommand(ADL in) throws CommandException {
		ensureNotNull(this, "delete", "fragmentId", fragmentId);

		ADLDocument doc = in.getAsDocument();
		in.getTranscoder().ensureCSSEngine(doc);
		
		validateFragmentHash(in);
		Element e = doc.getElementById(fragmentId);
		if (e==null) {
			throw new CommandException("Couldn't find fragment: "+fragmentId, this);
		}
		
		Node parent = e.getParentNode();
		DeleteType deleteType = getDeleteType(e);
	
		switch (deleteType) {
		case NONE:
			LOG.info("No delete of " + fragmentId);
			return in;
		case SINGLE:
			NodeList children = e.getChildNodes();
			while (children.getLength() > 0) {
				Node c = children.item(0);
				parent.insertBefore(c, e);
			}
			break;
		case CASCADE:
		}

		parent.removeChild(e);
		LOG.info("Processed delete of " + fragmentId);
		return in;
	}

	static DeleteType getDeleteType(Element e) {	
		if (e instanceof StyledKite9SVGElement) {
			EnumValue ev = (EnumValue) ((StyledKite9SVGElement) e).getCSSStyleProperty(CSSConstants.ELEMENT_TYPE_PROPERTY);
			DiagramElementType det = (DiagramElementType) ev.getTheValue();
			switch (det) {
			case CONTAINER:
				return DeleteType.SINGLE;
			case LINK_END:
			case DIAGRAM:
				return DeleteType.NONE;
			case NONE:
			case UNSPECIFIED:
			case SVG:
			case TEXT:
			case LINK:
				return DeleteType.CASCADE;
			}
		}
		
		return DeleteType.CASCADE;
	}
}
