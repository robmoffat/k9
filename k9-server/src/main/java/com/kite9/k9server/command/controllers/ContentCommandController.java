package com.kite9.k9server.command.controllers;

import java.io.InputStream;
import java.net.URI;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.kite9.framework.common.Kite9ProcessingException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kite9.k9server.adl.format.media.Format;
import com.kite9.k9server.adl.holder.ADL;
import com.kite9.k9server.command.Command;
import com.kite9.k9server.command.content.AbstractContentCommand;
import com.kite9.k9server.command.content.ContentAPI;
import com.kite9.k9server.security.Kite9HeaderMeta;

/**
 * This controls rendering and commands for individual diagrams.
 * 
 * @author robmoffat
 *
 */
@RestController
public class ContentCommandController extends AbstractCommandController {

	@GetMapping(path = { "/content/**" }, produces = MediaType.ALL_VALUE)
	public Object getContent(HttpServletRequest req, @RequestHeader HttpHeaders headers,
			@RequestParam(required = false, name = "revision", defaultValue = "") String revision,
			Authentication authentication) throws Exception {
		String url = req.getRequestURL().toString();
		String path = getPathToFileInRepo(url);
		Format f = fs.getFormatFor(path).orElseThrow();
		
		try {
			ContentAPI api = apiFactory.createAPI(authentication, path);
			InputStream is = api.getRevision(revision);
			ADL adl = f.handleRead(is, new URI(url), headers);
			Kite9HeaderMeta.addRegularMeta(adl, url, "Kite9 Editor");
			addUndoRedoMeta(adl, api);
			return adl;
		} catch (Exception e) {
			throw new Kite9ProcessingException("Couldn't get content for " + path, e);
		}
	}

	@PostMapping(path = { "/content/**" }, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.ALL_VALUE)
	public ADL update(@RequestHeader HttpHeaders headers, HttpServletRequest req,
			RequestEntity<List<Command>> reqEntity, Authentication authentication) throws Exception {

		String url = req.getRequestURL().toString();
		String path = getPathToFileInRepo(url);
		return fs.getFormatFor(path).map(f -> {
			return updateInner(headers, reqEntity, authentication, url, path, f);
		}).get();
	}

	protected ADL updateInner(HttpHeaders headers, RequestEntity<List<Command>> reqEntity, Authentication authentication,
			String url, String path, Format f) {
		try {
			ContentAPI api = apiFactory.createAPI(authentication, path);
			InputStream is = api.getRevision(null);
			ADL adl = f.handleRead(is, new URI(url), headers);
			adl = (ADL) performSteps(reqEntity.getBody(), adl, authentication, headers, new URI(url));
			AbstractContentCommand.persistContent(adl, f, api, "New Revision in Kite9 Editor");
			Kite9HeaderMeta.addRegularMeta(adl, url, "Kite9 Editor");
			addUndoRedoMeta(adl, api);
			return adl;
		} catch (Exception e) {
			throw new Kite9ProcessingException("Couldn't get content for "+path, e);
		}
	}

	private String getPathToFileInRepo(String url) {
		return url.substring(url.indexOf("/content/"));
	}

	private void addUndoRedoMeta(ADL adl, ContentAPI api) {
		// TODO Auto-generated method stub

	}
}