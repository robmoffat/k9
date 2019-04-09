package com.kite9.k9server.command;

import java.net.URI;
import java.net.URISyntaxException;

import org.kite9.diagram.dom.elements.ADLDocument;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.kite9.k9server.adl.holder.ADL;

public class Copy extends AbstractLocatedCommand {

	protected String uriStr;		// to insert.	
	protected String newId;
	
	public Copy() {
		super();
	}
	
	public Copy(String fragmentId, String fragmentHash, String beforefragmentId, String uriStr, String newId) {
		super(fragmentId, fragmentHash, beforefragmentId);
		this.uriStr = uriStr;
		this.newId = newId;
	}

	@Override
	public ADL applyCommand(ADL adl) throws CommandException {
		ensureNotNull(this, "copy", "uri", uriStr);
		
		try {
			ADLDocument doc = adl.getAsDocument();
			performCopy(adl.getUri(), doc, newId);
		} catch (Exception e) {
			throw new CommandException("Couldn't copy", e, this);
		}
		
		LOG.info("Processed insert into "+fragmentId);
		return adl;
	}

	protected Element performCopy(URI baseUri, ADLDocument doc, String newId) {
		Element insert = getForeignElementCopy(doc, baseUri, uriStr, true);
		doc.adoptNode(insert);
		
		insert(doc, insert);
		
		replaceIds(insert);
		
		if ((newId != null) && (doc.getElementById(newId) == null)) {
			insert.setAttribute("id", newId);
		}
		
		return insert;
	}


	protected void replaceIds(Element insert) {
		if (insert.hasAttribute("id")) {
			insert.setAttribute("id", ((ADLDocument) insert.getOwnerDocument()).createUniqueId()); 
		}
		
		NodeList children = insert.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			Node n = children.item(i);
			if (n instanceof Element) {
				replaceIds((Element) n);
			}
		}
	}

	
}
