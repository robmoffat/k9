package com.kite9.k9server.command;

import java.net.URI;
import java.net.URISyntaxException;

import org.kite9.diagram.dom.elements.ADLDocument;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.kite9.k9server.adl.holder.ADL;
import com.kite9.k9server.adl.holder.ADLImpl;

public class Copy extends AbstractLocatedCommand {

	protected String uriStr;		// to insert.	

	public Copy() {
		super();
	}
	
	public Copy(String fragmentId, String fragmentHash, String beforefragmentId, String uriStr) {
		super(fragmentId, fragmentHash, beforefragmentId);
		this.uriStr = uriStr;
	}

	@Override
	public ADL applyCommand(ADL adl) throws CommandException {
		ensureNotNull(this, "copy", "uri", uriStr);
		
		try {
			ADLDocument doc = adl.getAsDocument();
			performCopy(doc);
		} catch (Exception e) {
			throw new CommandException("Couldn't copy", e, this);
		}
		
		LOG.info("Processed insert into "+fragmentId);
		return adl;
	}

	protected Element performCopy(ADLDocument doc) throws URISyntaxException {
		Element insert = getElementToInsert(doc, uriStr);
		doc.adoptNode(insert);
		
		insert(doc, insert);
		
		replaceIds(insert);
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


	public Element getElementToInsert(ADLDocument currentDoc, String uriStr) throws URISyntaxException {
		URI uri = new URI(uriStr);
		String id = uri.getFragment();
		if (uriStr.startsWith("#")) {
			Element template = currentDoc.getElementById(id);
			Element out = (Element) template.cloneNode(true);	
			return out;
			
		} else {
			String fullUri = uri.toString();
			String documentUri = fullUri.substring(0, fullUri.length() - id.length());
			ADLDocument toInsert = new ADLImpl(documentUri).getAsDocument();
			Element out = toInsert.getElementById(id);
			return out;
		}
	}

	
}
