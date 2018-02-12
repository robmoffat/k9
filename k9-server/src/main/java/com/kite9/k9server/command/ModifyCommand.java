package com.kite9.k9server.command;

import org.kite9.framework.xml.ADLDocument;
import org.w3c.dom.Element;

import com.kite9.k9server.adl.holder.ADL;
import com.kite9.k9server.domain.Document;
import com.kite9.k9server.domain.DocumentRepository;
import com.kite9.k9server.domain.Revision;
import com.kite9.k9server.domain.RevisionRepository;
import com.kite9.k9server.domain.User;

/**
 * This command changes the contents of a single XML element to something new.  
 * If applied without a target argument, will replace the whole document.
 * 
 * @author robmoffat
 */
public class ModifyCommand extends NewRevisionCreatingCommand {
	
	String elementId;
	String oldContents;
	String modification;
	
	public ModifyCommand(Long docId, User author, String elementId, String oldContents, String modification) {
		super(docId, author);
		this.oldContents = oldContents;
		this.modification = modification;
		this.elementId = elementId;
	}

	@Override
	public Change applyCommand(Document d, DocumentRepository dr, RevisionRepository rr) throws CommandException {
		Revision r = d.getCurrentRevision();
		
		ADL adl = loadRevisionXML(dr);
		ADLDocument in = adl.getAsDocument();
		Element affected = in.getElementById(elementId);
		
		if (affected == null) {
			throw new CommandException("Couldn't find element: "+elementId, this);
		}
		
		String existingXML = adl.getAsXMLString(affected);
		if (!existingXML.equals(oldContents)) {
			throw new CommandException("XML Has Changed.  Existing: "+existingXML+"\n    Expected: "+oldContents, this);
		}
		
		// if ok, replace with "modification"
//		adl.getTranscoder().
		
		// save the new revision
		Revision newRevision = createNewRevision(d, r, dr, rr, adl.getAsXMLString(), getAuthor());
		
		return new Change(adl, newRevision);
	}

}
