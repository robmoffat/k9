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

	String uriStr;		// to insert.	

	@Override
	public void applyCommand(ADL adl) throws CommandException {
		ensureNotNull(this, "copy", "uri", uriStr);
		
		
		try {
			ADLDocument doc = adl.getAsDocument();
			Element insert = getElementToInsert(uriStr);
			doc.adoptNode(insert);
			
			insert(doc, insert);
			
			replaceIds(insert);
		} catch (Exception e) {
			throw new CommandException("Couldn't copy", e, this);
		}
		
		LOG.info("Processed insert into "+fragmentId);
	}


	private void replaceIds(Element insert) {
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


	public Element getElementToInsert(String uriStr) throws URISyntaxException {
		URI uri = new URI(uriStr);
		String documentUri = uri.toString();
		String id = uri.getFragment();
		documentUri.substring(0, documentUri.length() - id.length());
		
		ADLDocument toInsert = new ADLImpl(documentUri).getAsDocument();
		Element out = toInsert.getElementById(id);
		return out;
	}


}
