package com.kite9.k9server.command.controllers;

import java.io.InputStream;
import java.net.URI;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.kite9.framework.common.Kite9ProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import com.kite9.k9server.adl.format.FormatSupplier;
import com.kite9.k9server.adl.holder.ADL;
import com.kite9.k9server.command.Command;
import com.kite9.k9server.command.content.ContentAPI;
import com.kite9.k9server.command.content.ContentAPIFactory;
import com.kite9.k9server.command.content.ContentCommand;
import com.kite9.k9server.domain.Revision;
import com.kite9.k9server.security.Kite9HeaderMeta;

@Controller
public class ContentCommandController extends AbstractCommandController {

	@Autowired
	protected ContentAPIFactory apiFactory;
	
	@Autowired
	FormatSupplier fs;
	
	@GetMapping(path =  {"/content/**" },  produces = MediaType.ALL_VALUE)
	public Object getContent(
		HttpServletRequest req, 
		@RequestHeader HttpHeaders headers,
		@RequestParam(required = false, name = "revision", defaultValue = "") String revision,
		Authentication authentication) throws Exception {
		String url = req.getRequestURL().toString();
		String path = getPathToFileInRepo(url);
		return fs.getFormatFor(path).map(f -> {
			try {
				ContentAPI api = apiFactory.createAPI(authentication, path);
				InputStream is = api.getRevision(revision);
				ADL adl = f.handleRead(is, new URI(url), headers);
				Kite9HeaderMeta.addRegularMeta(adl, url, "Kite9 Editor");
				addUndoRedoMeta(adl, api);
				return adl;
			} catch (Exception e) {
				throw new Kite9ProcessingException("Couldn't get content for "+path, e);
			}
			
		}).get();
	}

	@PostMapping(
			path =  {"/content/**"}, 
			consumes = MediaType.APPLICATION_JSON_VALUE,
			produces = MediaType.ALL_VALUE)
	public ADL update(
			@RequestHeader HttpHeaders headers,
			HttpServletRequest req, 
			RequestEntity<List<Command>> reqEntity,
			Authentication authentication) throws Exception {
		
		String url = req.getRequestURL().toString();
		String path = getPathToFileInRepo(url);
		return fs.getFormatFor(path).map(f -> {
			try {
				ContentAPI api = apiFactory.createAPI(authentication, path);
				InputStream is = api.getRevision(null);
				ADL adl = f.handleRead(is, new URI(url), headers);
				Kite9HeaderMeta.addRegularMeta(adl, url, "Kite9 Editor");
				addUndoRedoMeta(adl, api);
				return adl;
			} catch (Exception e) {
				throw new Kite9ProcessingException("Couldn't get content for "+path, e);
			}
			
		}).get();
		
		
		
		
	}

	private String getPathToFileInRepo(String url) {
		return url.substring(url.indexOf("/content/")+8);
	}

	private void addUndoRedoMeta(ADL adl, ContentAPI api) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void embellishCommand(Object input, HttpHeaders headers, URI url, Command command) throws Exception {
		super.embellishCommand(input, headers, url, command);
		Authentication a = SecurityContextHolder.getContext().getAuthentication();

		if (command instanceof ContentCommand) {
			((ContentCommand) command).setContentApi(apiFactory.createApiFor(a, url.toString()), headers, a, fs);
		}
	}
}