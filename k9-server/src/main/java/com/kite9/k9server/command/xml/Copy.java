package com.kite9.k9server.command.xml;

import java.net.URI;

import org.kite9.diagram.dom.elements.ADLDocument;
import org.springframework.http.HttpStatus;
import org.w3c.dom.Element;

import com.kite9.k9server.adl.holder.ADL;
import com.kite9.k9server.command.CommandException;

public class Copy extends AbstractLocatedCommand {

	protected String uriStr;		// to insert.	
	protected String newId;
	protected boolean deep;
	
	
	public Copy() {
		super();
	}
	
	public Copy(String fragmentId, ADL on, String beforeFragmentId, String uriStr, String newId, boolean deep) {
		super(fragmentId, on, beforeFragmentId);
		this.uriStr = uriStr;
		this.newId = newId;
		this.deep = deep;
	}

	@Override
	public ADL applyCommand() throws CommandException {
		ensureNotNull(this, "copy", "uri", uriStr);
		
		try {
			ADLDocument doc = adl.getAsDocument();
			performCopy(adl.getUri(), doc, newId, adl);
		} catch (Exception e) {
			throw new CommandException(HttpStatus.CONFLICT, "Couldn't copy", e, this);
		}
		
		LOG.info("Processed copy into "+fragmentId);
		return adl;
	}

	protected Element performCopy(URI baseUri, ADLDocument doc, String newId, ADL adl) {
		Element insert = getForeignElementCopy(doc, baseUri, uriStr, deep, adl);
		doc.adoptNode(insert);
		
		insert(doc, insert);
		ensureParentElements(insert.getParentNode(), insert);
		
		replaceIds(insert);
		
		if ((newId != null) && (doc.getElementById(newId) == null)) {
			insert.setAttribute("id", newId);
		}
		
		return insert;
	}

	
}
