package com.kite9.k9server.adl.command;

import java.util.List;

import com.kite9.k9server.adl.holder.ADL;
import com.kite9.k9server.command.CommandException;
import com.kite9.k9server.command.Replace;

/**
 * Checks the consistency of the document's links.
 * @author robmoffat
 */
public class ADLReplace extends Replace {

	public ADLReplace() {
		super();
	}

	public ADLReplace(String fragmentId, String fragmentHash, String fromUri, boolean replaceContents, List<String> keptAttributes) {
		super(fragmentId, fragmentHash, fromUri, replaceContents, keptAttributes);
	}

	@Override
	public ADL applyCommand(ADL adl) throws CommandException {
		ADL out = super.applyCommand(adl);
		ADLReferenceHandler.checkConsistency(out.getAsDocument().getDocumentElement());
		return out;
	}

	
}
