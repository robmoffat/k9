package com.kite9.k9server.command.xml.adl;

import java.util.List;

import org.kite9.diagram.dom.elements.ADLDocument;

import com.kite9.k9server.adl.holder.ADL;
import com.kite9.k9server.command.CommandException;
import com.kite9.k9server.command.xml.Replace;

/**
 * Checks the consistency of the document's links.
 * @author robmoffat
 */
public class ADLReplace extends Replace {

	public ADLReplace() {
		super();
	}


	public ADLReplace(String fragmentId, ADL on, String fromUri, Approach approach, List<String> keptAttributes) {
		super(fragmentId, on, fromUri, approach, keptAttributes);
	}


	@Override
	public ADL applyCommand() throws CommandException {
		ADL out = super.applyCommand();
		ADLDocument d = out.getAsADLDocument();
		adl.getTranscoder().ensureCSSEngine(d);
		ADLReferenceHandler.checkConsistency(d.getDocumentElement());
		return out;
	}

	
}
