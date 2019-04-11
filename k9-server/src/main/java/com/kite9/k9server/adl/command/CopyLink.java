package com.kite9.k9server.adl.command;

import java.util.Arrays;
import java.util.List;

import org.kite9.diagram.dom.elements.ADLDocument;
import org.w3c.dom.Element;

import com.kite9.k9server.adl.holder.ADL;
import com.kite9.k9server.command.CommandException;
import com.kite9.k9server.command.Copy;

public class CopyLink extends Copy {

	protected String fromId, toId;
	
	public CopyLink() {
		super();
	}

	public CopyLink(String fragmentId, String templateUri, String fromId, String toId, String linkId) {
		super(fragmentId, null, null, templateUri, linkId);
		this.fromId = fromId;
		this.toId = toId;
	}

	@Override
	public ADL applyCommand(ADL adl) throws CommandException {
		ensureNotNull(this, "create-link", "uri", uriStr);
		ensureNotNull(this, "create-link", "from", fromId);
		ensureNotNull(this, "create-link", "to", toId);
		
		try {
			ADLDocument doc = adl.getAsDocument();
			Element insert = performCopy(adl.getUri(), doc, newId, adl);
						
			List<String> refs = Arrays.asList(fromId, toId);
		
			// this means we can use css to find out the purpose of each element.
			adl.getTranscoder().ensureCSSEngine(doc);
			ADLReferenceHandler.hardcodedReferenceReplace(insert, refs);
			
		} catch (Exception e) {
			throw new CommandException("Couldn't create Link", e, this);
		}
		
		LOG.info("Processed createLink into "+fragmentId);
		return adl;
		
	}

	
}
