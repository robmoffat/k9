package com.kite9.k9server.command;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.kite9.k9server.adl.holder.ADL;
import com.kite9.k9server.domain.Document;
import com.kite9.k9server.domain.Revision;
import com.kite9.k9server.repos.DocumentRepository;

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

	@Transactional
	@RequestMapping(path="/api/v1/command", consumes= {MediaType.APPLICATION_JSON_UTF8_VALUE, MediaTypes.HAL_JSON_VALUE})
	public @ResponseBody ADL applyCommand(@RequestBody Command input) {
		// commands are always applied to the active revision of the document (if possible)
		
		
		// do security checks - tbc
		
		
		Revision r = d.getCurrentRevision();
		
		input.
		
	}
}
