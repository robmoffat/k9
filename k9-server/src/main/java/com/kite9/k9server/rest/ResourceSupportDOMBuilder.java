package com.kite9.k9server.rest;

import java.net.URI;

import org.springframework.hateoas.ResourceSupport;
import org.springframework.hateoas.Resources;
import org.springframework.http.HttpHeaders;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.kite9.k9server.adl.holder.ADL;
import com.kite9.k9server.adl.holder.ADLImpl;
import com.kite9.k9server.domain.Entity;

public class ResourceSupportDOMBuilder {

	public ADL createDocument(ResourceSupport rs, String template, URI u, HttpHeaders headers) {
		ADL container = new ADLImpl(template, u, headers);
		
		NodeList nl = container.getAsDocument().getRootElement().getElementsByTagName("diagram");
		
		if (nl.getLength() != 1) {
			throw new IllegalArgumentException("Couldn't find single diagram element in template");
		}
		
		Element top = (Element) nl.item(0);
		Element main = createElementFrom(rs, true, container.getAsDocument());	
		top.appendChild(main);
		return container;
	}

	private Element createElementFrom(ResourceSupport rs, boolean topLevel, Document d) {
		if (rs instanceof Entity) {
			Element entity = d.createElement("entity");
			
			
		} else if (rs instanceof Resources) {
			
		} else {
			
		}
	}
}
