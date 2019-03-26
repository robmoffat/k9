package com.kite9.k9server.adl.command;

import java.util.Arrays;
import java.util.List;

import org.kite9.diagram.dom.CSSConstants;
import org.kite9.diagram.dom.elements.ADLDocument;
import org.kite9.diagram.dom.elements.StyledKite9XMLElement;
import org.kite9.diagram.dom.managers.EnumValue;
import org.kite9.diagram.model.style.DiagramElementType;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.kite9.k9server.adl.holder.ADL;
import com.kite9.k9server.command.CommandException;
import com.kite9.k9server.command.Copy;

public class CreateLink extends Copy {

	protected String fromId, toId, linkId;
	
	public CreateLink() {
		super();
	}

	public CreateLink(String fragmentId, String templateUri, String fromId, String toId, String linkId) {
		super(fragmentId, null, null, templateUri);
		this.fromId = fromId;
		this.toId = toId;
		this.linkId = linkId;
	}

	@Override
	public ADL applyCommand(ADL adl) throws CommandException {
		ensureNotNull(this, "create-link", "uri", uriStr);
		ensureNotNull(this, "create-link", "from", fromId);
		ensureNotNull(this, "create-link", "to", toId);
		
		try {
			ADLDocument doc = adl.getAsDocument();
			Element insert = performCopy(adl.getUri(), doc);
			
			if ((linkId != null) && (doc.getElementById(linkId) == null)) {
				insert.setAttribute("id", linkId);
			}
						
			List<String> refs = Arrays.asList(fromId, toId);
		
			// this means we can use css to find out the purpose of each element.
			adl.getTranscoder().ensureCSSEngine(doc);
			hardcodedReferenceReplace(insert, refs);
			
		} catch (Exception e) {
			throw new CommandException("Couldn't create Link", e, this);
		}
		
		LOG.info("Processed createLink into "+fragmentId);
		return adl;
		
	}

	/**
	 * This is hard-coded for now, but in the future we need to use xpaths / css attributes.
	 * somehow, this is the concern of Kite9Visualization rather than here..
	 */
	@Deprecated
	public void hardcodedReferenceReplace(Element insert, List<String> refs) {
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

	
}
