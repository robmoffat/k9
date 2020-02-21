package com.kite9.k9server.adl.holder;

import static org.apache.batik.util.SVGConstants.SVG_NAMESPACE_URI;

import java.util.Base64;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.kite9.k9server.adl.format.media.Kite9MediaTypes;

/**
 * Handles placing the ADL content within the SVG document. 
 * 
 */
public class Payload {

	public static final String ADL_MARKUP_ID = "adl:markup";

	public static void insertEncodedADLInSVG(ADL adl, Document svgDocument) {
		byte[] bytes = adl.getAsADLString().getBytes();
		String base64Encoded = new String(Base64.getEncoder().encode(bytes));
		
		Element adlScriptTag = svgDocument.getElementById(ADL_MARKUP_ID);
		if (adlScriptTag == null) {
			adlScriptTag = createScriptTag(svgDocument);
		}
		
		adlScriptTag.setTextContent(base64Encoded);
	}

	private static Element createScriptTag(Document svgDocument) {
		NodeList nl = svgDocument.getElementsByTagNameNS(SVG_NAMESPACE_URI, "defs");
		Element firstDef = null;
		if (nl.getLength() == 0) {
			Element documentElement = svgDocument.getDocumentElement();
			firstDef = svgDocument.createElementNS(SVG_NAMESPACE_URI, "defs");
			Node firstChild = documentElement.getChildNodes().item(0);
			documentElement.insertBefore(firstDef, firstChild);
		} else {
			firstDef = (Element) nl.item(0);
		}
		
		// add the script tag
		Element scriptTag = svgDocument.createElementNS(SVG_NAMESPACE_URI, "script");
		scriptTag.setAttribute("type", Kite9MediaTypes.ADL_SVG_VALUE+";base64");
		scriptTag.setAttribute("id", ADL_MARKUP_ID);
		
		firstDef.appendChild(scriptTag);
		return scriptTag;
	}

}
