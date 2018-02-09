package com.kite9.k9server.command;

import org.springframework.data.rest.webmvc.ResourceNotFoundException;

import com.kite9.k9server.adl.holder.ADL;
import com.kite9.k9server.adl.holder.ADLImpl;
import com.kite9.k9server.domain.Document;
import com.kite9.k9server.domain.Revision;
import com.kite9.k9server.domain.User;
import com.kite9.k9server.repos.DocumentRepository;

public abstract class AbstractCommand implements Command {
	
	public AbstractCommand(long docId, User author) {
		super();
		this.docId = docId;
		this.author = author;
	}

	private final long docId;
	private final User author;

	public User getAuthor() {
		return author;
	}

	@Override
	public long getDocumentId() {
		return docId;
	}
	
	public ADL loadRevisionXML(DocumentRepository dr) {
		
		Document d = dr.findOne(docId);
		
		if (d == null) {
			throw new ResourceNotFoundException();
		}
		
		Revision currentRevision = d.getCurrentRevision();
		
		if (currentRevision == null) {
			return null;
		} else {
			String xml = currentRevision.getInputXml();
			return new ADLImpl(xml, "somewhere");
		}
	}
}
