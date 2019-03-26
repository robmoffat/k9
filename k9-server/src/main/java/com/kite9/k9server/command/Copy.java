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
			performCopy(adl.getUri(), doc);
		} catch (Exception e) {
			throw new CommandException("Couldn't copy", e, this);
		}
		
		LOG.info("Processed insert into "+fragmentId);
		return adl;
	}

	protected Element performCopy(URI baseUri, ADLDocument doc) throws URISyntaxException {
		Element insert = getElementToInsert(doc, baseUri, uriStr);
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


	public Element getElementToInsert(ADLDocument currentDoc, URI baseUri, String uriStr) throws URISyntaxException {
		String id = uriStr.substring(uriStr.indexOf("#")+1);
		String location = uriStr.substring(0, uriStr.indexOf("#"));
		
		if (location.length() > 0) {
			// referencing a different doc.
			URI uri = baseUri.resolve(location);
			currentDoc = new ADLImpl(uri).getAsDocument();
		} 
		
		Element template = currentDoc.getElementById(id);
		Element out = (Element) template.cloneNode(true);	
		return out;
	}

	
}
