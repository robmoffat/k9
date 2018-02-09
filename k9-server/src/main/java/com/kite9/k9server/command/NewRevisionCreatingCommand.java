package com.kite9.k9server.command;

import com.kite9.k9server.domain.Document;
import com.kite9.k9server.domain.Revision;
import com.kite9.k9server.domain.User;
import com.kite9.k9server.repos.DocumentRepository;
import com.kite9.k9server.repos.RevisionRepository;
import com.kite9.k9server.security.Hash;

/**
 * Knows how to create a new revision in the database with some new xml.
 * 
 * @author robmoffat
 */
public abstract class NewRevisionCreatingCommand extends AbstractCommand {
	
	public NewRevisionCreatingCommand(long docId, User author) {
		super(docId, author);
	}

	public Revision createNewRevision(Document d, Revision old, DocumentRepository dr, RevisionRepository rr, String newXml, User author) {
		Revision change = new Revision();
		change.setAuthor(author);
		change.setDocument(d);
		change.setPreviousRevision(old);
		change.setInputXml(newXml);
		change.setDiagramHash(Hash.generateHash(newXml));
		
		// commit changes
		Revision saved = rr.save(change);
		old.setNextRevision(saved);
		rr.save(old);
		d.setCurrentRevision(saved);
		dr.save(d);
		return change;
	}
}
