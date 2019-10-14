package com.kite9.k9server.command.controllers;

import java.net.URI;
import java.util.List;

import org.kite9.framework.logging.Logable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.core.mapping.ResourceMappings;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.kite9.k9server.adl.holder.ADL;
import com.kite9.k9server.adl.holder.ADLImpl;
import com.kite9.k9server.command.Command;
import com.kite9.k9server.domain.user.UserRepository;

/**
 * Accepts commands to the system in order to modify XML.  Contents are returned back in whatever format is
 * requested.
 * 
 * @author robmoffat
 *
 */
@Controller 
public class StaticCommandController extends AbstractCommandController implements Logable {
		
	@Autowired
	UserRepository userRepository;
	
	@Autowired
	ResourceMappings mappings;
	
	@RequestMapping(method={RequestMethod.POST}, path="/api/command/v1", consumes= {MediaType.APPLICATION_JSON_VALUE})
	public @ResponseBody ADL applyCommandOnStatic (
			RequestEntity<List<Command>> req,
			@RequestParam(required=true, name="on") String sourceUri) throws Exception {
		
		URI uri = new URI(sourceUri);
		URI base = req.getUrl();
		uri = base.resolve(uri);
		ADL input = new ADLImpl(uri, req.getHeaders());
		
		return performXMLCommands(req.getBody(), input, null, req.getHeaders(), uri);
	}

}