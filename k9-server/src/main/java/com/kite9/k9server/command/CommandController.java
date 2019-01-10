package com.kite9.k9server.command;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.kite9.k9server.adl.format.formattable.AbstractFormattable;
import com.kite9.k9server.adl.holder.ADL;
import com.kite9.k9server.domain.document.Document;
import com.kite9.k9server.domain.document.DocumentRepository;
import com.kite9.k9server.domain.revision.Revision;
import com.kite9.k9server.domain.revision.RevisionRepository;
import com.kite9.k9server.domain.user.User;
import com.kite9.k9server.security.Hash;

/**
 * Accepts commands to the system in order to modify XML.  Contents are returned back in whatever format is
 * requested.
 * 
 * @author robmoffat
 *
 */
@Controller
public class CommandController {
		
	@Autowired
	DocumentRepository docRepo;
	
	@Autowired
	RevisionRepository revisionRepo;
	
	@Transactional
	@RequestMapping(path="/api/v1/command", consumes= {MediaType.APPLICATION_JSON_UTF8_VALUE, MediaTypes.HAL_JSON_VALUE})
	public @ResponseBody Revision applyCommand(@RequestBody Command input) throws CommandException {
		// commands are always applied to the active revision of the document (if possible)
		Document d = input.getDocument();
		
		// do security checks - tbc
		
		Revision currentRevision = d.getCurrentRevision();
		ADL adl = null;
		if (currentRevision != null) {
			adl = currentRevision.getInput();
		}
		
		adl = input.applyCommand(adl);
		return createNewRevision(d, currentRevision, adl.getAsXMLString(), null, input.getAuthor());
	}
		
	public Revision createNewRevision(Document d, Revision old, String newXml, String renderedXml, User author) {
		Revision change = new Revision();
		change.setAuthor(author);
		change.setDocument(d);
		change.setPreviousRevision(old);
		change.setInputXml(newXml);
		change.setOutputXml(renderedXml);
		change.setDiagramHash(Hash.generateHash(newXml));
		
		// commit changes
		Revision saved = revisionRepo.save(change);
		return saved;
	}
}
