package com.kite9.k9server.command;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.kite9.k9server.adl.holder.ADL;

/**
 * Accepts commands to the system in order to modify XML.  Contents are returned back in whatever format is
 * requested.
 * 
 * @author robmoffat
 *
 */
@Controller
public class StepsCommandController {
	
	
	@Transactional
	@RequestMapping(method={RequestMethod.POST}, path="/api/v1/command", consumes= {MediaType.APPLICATION_JSON_VALUE})
	public @ResponseBody ADL applyCommand(@RequestBody StepsCommand input) throws CommandException {
		return input.applyCommand();
	}
	
}
