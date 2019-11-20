package com.kite9.k9server.command.xml.adl;

import org.kite9.diagram.dom.CSSConstants;
import org.kite9.diagram.dom.elements.ADLDocument;
import org.kite9.diagram.dom.elements.ReferencingKite9XMLElement;
import org.springframework.http.HttpStatus;
import org.w3c.dom.Element;

import com.kite9.k9server.adl.holder.ADL;
import com.kite9.k9server.command.CommandException;
import com.kite9.k9server.command.xml.Copy;

public class CopyLink extends Copy {

	protected String fromId, toId;
	
	public CopyLink() {
		super();
		this.deep = true;
	}

	public CopyLink(String fragmentId, String templateUri, String fromId, String toId, String linkId) {
		super(fragmentId, null, null, templateUri, linkId, true);
		this.fromId = fromId;
		this.toId = toId;
		this.deep = true;
	}

	@Override
	public ADL applyCommand() throws CommandException {
		ensureNotNull(this, "create-link", "uri", uriStr);
		ensureNotNull(this, "create-link", "fromId", fromId);
		ensureNotNull(this, "create-link", "toId", toId);
		
		try {
			ADLDocument doc = adl.getAsADLDocument();
			Element insert = performCopy(adl.getUri(), doc, newId, adl);
		
			// this means we can use css to find out the purpose of each element.
			adl.getTranscoder().ensureCSSEngine(doc);
			
			if (insert instanceof ReferencingKite9XMLElement) {
				((ReferencingKite9XMLElement) insert).setIDReference(CSSConstants.LINK_FROM_XPATH, fromId);
				((ReferencingKite9XMLElement) insert).setIDReference(CSSConstants.LINK_TO_XPATH, toId);
			}
			
		} catch (Exception e) {
			throw new CommandException(HttpStatus.EXPECTATION_FAILED, "Couldn't create Link", e, this);
		}
		
		LOG.info("Processed createLink into "+fragmentId);
		return adl;
		
	}

	
}
