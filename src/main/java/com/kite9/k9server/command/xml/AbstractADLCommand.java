package com.kite9.k9server.command.xml;

import java.net.URI;

import org.apache.batik.dom.AbstractAttr;
import org.apache.batik.dom.AbstractElement;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kite9.diagram.dom.elements.ADLDocument;
import org.springframework.http.HttpStatus;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kite9.k9server.adl.holder.ADL;
import com.kite9.k9server.command.Command;
import com.kite9.k9server.command.CommandException;
import com.kite9.k9server.command.XMLCommand;

/**
 * Contains a hash, which is used to make sure that the command is operating on a fresh version
 * of the data.
 * 
 * @author robmoffat
 *
 */
public abstract class AbstractADLCommand implements XMLCommand {
	
	protected String fragmentId;
	
	@JsonProperty(required = false)
	protected String base64adl;
	protected ADL adl;
	
	public AbstractADLCommand() {
		super();
	}

	public AbstractADLCommand(ADL on, String fragmentId) {
		super();
		this.fragmentId = fragmentId;
		this.adl = on;
	}
	
	@Override
	public void setOn(ADL on) {
		this.adl = on;
	}
	
	@Override
	public String getBase64EncodedState() {
		return base64adl;
	}

	public static final Log LOG = LogFactory.getLog(Command.class);
	
	public static void ensureNotNull(Command c, String operation, String field, Object n) throws CommandException {
		if (n == null) {
			throw new CommandException(HttpStatus.CONFLICT, operation+" requires "+field+" to be set", c);
		}
	}
	
	protected Element findFragmentElement(ADLDocument doc) {
		Element into = doc.getElementById(fragmentId);
		if (into == null) {
			throw new CommandException(HttpStatus.NOT_FOUND, "Could not locate: "+fragmentId, this);
		}
		return into;
	}

	public Element getForeignElementCopy(ADLDocument currentDoc, URI baseUri, String uriStr, boolean deep, ADL context) {
		String id = uriStr.substring(uriStr.indexOf("#")+1);
		String location = uriStr.substring(0, uriStr.indexOf("#"));
		
		if (location.length() > 0) {
			// referencing a different doc.
			URI uri = baseUri.resolve(location);
			currentDoc = context.loadRelatedDocument(uri);
		} 
		
		Element template = currentDoc.getElementById(id);
		Element out = (Element) template.cloneNode(deep);	
		return out;
	}

	/**
	 * For some reason, in the clone node process, batik clears the parent element setting, and this doesn't get fixed.
	 */
	protected void ensureParentElements(Node parent, Node child) {
		if ((child instanceof Attr) && (((Attr)child).getOwnerElement() == null)) {
			((AbstractAttr)child).setOwnerElement((AbstractElement) parent);
		}
		
		if (child instanceof Element) {
			NodeList children = child.getChildNodes();
			for (int i = 0; i < children.getLength(); i++) {
				ensureParentElements(child, children.item(i));
			}
			
			NamedNodeMap map = child.getAttributes();
			for (int i = 0; i < map.getLength(); i++) {
				ensureParentElements(child, map.item(i));
			}
		}
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
