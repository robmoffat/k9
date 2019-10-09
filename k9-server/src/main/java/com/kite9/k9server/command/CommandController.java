package com.kite9.k9server.command;

import java.net.URI;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.support.Repositories;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.kite9.k9server.adl.holder.ADL;
import com.kite9.k9server.adl.holder.ADLImpl;
import com.kite9.k9server.command.domain.DomainCommand;
import com.kite9.k9server.command.xml.ADLCommand;
import com.kite9.k9server.domain.RestEntity;
import com.kite9.k9server.domain.SecuredCrudRepository;

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
	Repositories repositories;
	
	@RequestMapping(method={RequestMethod.POST}, path="/api/command/v1", consumes= {MediaType.APPLICATION_JSON_VALUE})
	public @ResponseBody ADL applyCommand(
			RequestEntity<List<Command>> req,
			@RequestParam(required=true, name="on") String sourceUri) throws Exception {
		
		URI uri = new URI(sourceUri);
		URI base = req.getUrl();
		uri = base.resolve(uri);
		ADL input = new ADLImpl(uri, req.getHeaders());
		
		return applyCommand(req.getBody(), input, null, req.getHeaders(), uri);
	}
	
	/**
	 * This is used for applying commands to domain objects.
	 */
	@RequestMapping(method={RequestMethod.POST}, path="/api/{repository}/{id}/change", consumes= {MediaType.APPLICATION_JSON_VALUE}) 
	public @ResponseBody ADL applyCommand(
				RequestEntity<List<Command>> req,
				@RequestParam(required=true, name="repository") String repository,
				@RequestParam(required=true, name="id") String id) throws Exception {
		
		
		return null;
		
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public ADL applyCommand(List<Command> steps, ADL input, RestEntity context, HttpHeaders headers, URI url) {
		for (Command command : steps) {
			if (command instanceof ADLCommand) {
				((ADLCommand) command).setOn(input);
			}
			
			if (command instanceof DomainCommand) {
				Object repo = repositories.getRepositoryFor(context.getClass()).orElseThrow(() -> new CommandException("No repository for "+context.getClass(), command));
				((DomainCommand)command).setCommandContext((SecuredCrudRepository) repo, context, url, headers);
			}
		
			input = command.applyCommand();
		}
		
		return input;
	}
}
