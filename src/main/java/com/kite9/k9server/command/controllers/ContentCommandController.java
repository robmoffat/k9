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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.kite9.k9server.adl.format.media.Format;
import com.kite9.k9server.adl.holder.ADL;
import com.kite9.k9server.command.Command;
import com.kite9.k9server.command.XMLCommand;
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
			Authentication authentication) throws Exception {
		String url = req.getRequestURL().toString();
		String path = getPathToFileInRepo(url);
		Format f = fs.getFormatFor(path).orElseThrow(); 
		
		try {
			ContentAPI api = apiFactory.createAPI(authentication, url);
			InputStream is = api.getCurrentRevisionContent();
			ADL adl = f.handleRead(is, new URI(url), headers);
			Kite9HeaderMeta.addRegularMeta(adl, url, "Kite9 Editor");
			api.addMeta(adl);
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
		Format f = fs.getFormatFor(path).orElseThrow();
		return updateInner(headers, reqEntity, authentication, url, path, f, needsCommit(reqEntity.getBody()));
	}

	private boolean needsCommit(List<Command> body) {
		return body.stream().filter(c -> c instanceof XMLCommand).findFirst().isPresent();
	}

	protected ADL updateInner(HttpHeaders headers, RequestEntity<List<Command>> reqEntity, Authentication authentication,
			String url, String path, Format f, boolean commit) {
		try {
			ContentAPI api = apiFactory.createAPI(authentication, url);
			InputStream is = api.getCurrentRevisionContent();
			ADL adl = f.handleRead(is, new URI(url), headers);
			adl = (ADL) performSteps(reqEntity.getBody(), adl, authentication, headers, new URI(url));
			if (commit) {
				AbstractContentCommand.persistContent(adl, f, api, "Changed "+path+" in Kite9 Editor");
			}
			Kite9HeaderMeta.addRegularMeta(adl, url, "Kite9 Editor");
			api.addMeta(adl);
			return adl;
		} catch (Exception e) {
			throw new Kite9ProcessingException("Couldn't get content for "+path, e);
		}
	}

	private String getPathToFileInRepo(String url) {
		return url.substring(url.indexOf("/content/"));
	}
}