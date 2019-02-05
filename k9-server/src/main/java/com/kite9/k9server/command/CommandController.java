package com.kite9.k9server.command;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

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
	@RequestMapping(method={RequestMethod.POST}, path="/api/v1/command", consumes= {MediaType.APPLICATION_JSON_UTF8_VALUE, MediaTypes.HAL_JSON_VALUE})
	public ModelAndView applyCommand(@RequestBody Command input) throws CommandException {
		
		
	}
	
	@Transactional
	@RequestMapping(method={RequestMethod.GET}, path="/api/v1/new")
	public ModelAndView newDocument() throws CommandException {
		return ModelAndView.
	}
		
	public Revision createNewRevision(Document d, Revision old, String newXml, User author) {
		Revision change = new Revision();
		change.setAuthor(author);
		change.setDocument(d);
		change.setPreviousRevision(old);
		change.setXml(newXml);
		change.setDiagramHash(Hash.generateHash(newXml));
		
		// commit changes
		Revision saved = revisionRepo.save(change);
		return saved;
	}
}
