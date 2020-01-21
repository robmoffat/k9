package com.kite9.k9server.command.controllers;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;

import org.kite9.framework.logging.Logable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.BasePathAwareController;
import org.springframework.data.rest.webmvc.PersistentEntityResourceAssembler;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.server.ResponseStatusException;

import com.kite9.k9server.adl.format.media.Kite9MediaTypes;
import com.kite9.k9server.command.Command;
import com.kite9.k9server.command.CommandException;
import com.kite9.k9server.domain.entity.RestEntity;
import com.kite9.k9server.domain.user.UserRepository;

/**
 * Accepts commands to the system in order to modify domain objects (resources).
 * 
 * For posts to /admin
 * 
 * @author robmoffat
 *
 */
@BasePathAwareController 
public class AdminCommandController extends AbstractCommandController implements Logable {
	
	@Autowired
	UserRepository userRepository;
		
	/**
	 * This is used for applying commands to domain objects.
	 */
	@RequestMapping(method={RequestMethod.POST}, 
		path= {"/admin"}, 
		consumes= {MediaType.APPLICATION_JSON_VALUE},
		produces= {
			MediaTypes.HAL_JSON_VALUE, 
			Kite9MediaTypes.ADL_SVG_VALUE, 
			Kite9MediaTypes.SVG_VALUE,
			MediaType.APPLICATION_JSON_VALUE
		}) 
	@ResponseBody
	@Transactional
	public Object applyCommandOnResource (
				RequestEntity<List<Command>> req,
				HttpServletRequest request,
				PersistentEntityResourceAssembler assembler) throws CommandException {
				
		try {
			Object out = performSteps(req.getBody(), null, null, req.getHeaders(), req.getUrl());
			if (out instanceof RestEntity) {
				return assembler.toFullResource(out);
			} else {
				return out;
			}
		} catch (ResponseStatusException e) {
			throw e;
		} catch (Throwable e) {
			throw new CommandException(HttpStatus.CONFLICT, "Couldn't process commands", e, req.getBody());
		} 
	}

	@Override
	public String getPrefix() {
		return "RCC ";
	}

}
