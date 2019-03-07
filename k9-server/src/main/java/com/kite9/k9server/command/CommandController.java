package com.kite9.k9server.command;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.kite9.k9server.adl.holder.ADL;
import com.kite9.k9server.adl.holder.ADLImpl;

/**
 * Accepts commands to the system in order to modify XML.  Contents are returned back in whatever format is
 * requested.
 * 
 * @author robmoffat
 *
 */
@Controller
public class CommandController {
	
	@RequestMapping(method={RequestMethod.POST}, path="/api/command/v1", consumes= {MediaType.APPLICATION_JSON_VALUE})
	public @ResponseBody ADL applyCommand(
			@RequestBody List<Command> steps,
			@RequestParam(required=true, name="on") String sourceUri) throws CommandException {
		
		ADL input = new ADLImpl(sourceUri);
		
		return applyCommand(steps, input);
	}
	
	public ADL applyCommand(List<Command> steps, ADL input) {
		for (Command command : steps) {
			input = command.applyCommand(input);
		}
		
		return input;
	}
}
