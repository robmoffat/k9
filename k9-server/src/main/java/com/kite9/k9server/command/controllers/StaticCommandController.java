package com.kite9.k9server.command.controllers;

import java.net.URI;
import java.util.List;

import org.kite9.framework.logging.Logable;
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

/**
 * Applies commands to given xml url, but no persistence done.
 * 
 * @author robmoffat
 *
 */
@Controller 
public class StaticCommandController extends AbstractCommandController implements Logable {
		
	@RequestMapping(method={RequestMethod.POST}, path="/api/command/v1", consumes= {MediaType.APPLICATION_JSON_VALUE})
	public @ResponseBody ADL applyCommandOnStatic (
			RequestEntity<List<Command>> req,
			@RequestParam(required=true, name="on") String sourceUri) throws Exception {
		
		URI uri = new URI(sourceUri);
		URI base = req.getUrl();
		uri = base.resolve(uri);
		ADL input = ADLImpl.uriMode(uri, req.getHeaders());
		
		return (ADL) performSteps(req.getBody(), input, null, req.getHeaders(), uri);
	}
	
}
