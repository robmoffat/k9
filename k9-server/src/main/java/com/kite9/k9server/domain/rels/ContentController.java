package com.kite9.k9server.domain.rels;

import java.net.URI;

import javax.servlet.http.HttpServletRequest;

import org.kite9.framework.common.Kite9ProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.BasePathAwareController;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.server.ResponseStatusException;

import com.kite9.k9server.adl.holder.ADL;
import com.kite9.k9server.adl.holder.ADLImpl;
import com.kite9.k9server.domain.document.Document;
import com.kite9.k9server.domain.document.DocumentRepository;
import com.kite9.k9server.domain.revision.Revision;
import com.kite9.k9server.domain.revision.RevisionRepository;

/**
 * This allows elements to respond to the /content url.
 * @author robmoffat
 *
 */
@BasePathAwareController
public class ContentController {
	
	@Autowired
	DocumentRepository documents;
	
	@Autowired
	RevisionRepository revisions;

	@GetMapping(path="/{repository}/{id}/content")
	public @ResponseBody ADL content(
			@PathVariable("repository") String repository,
			@PathVariable("id") long id,
			HttpServletRequest request,
			@RequestHeader HttpHeaders headers) throws Exception {
		
		String uri = request.getRequestURI();
		
		if (repository.equals("documents")) {
			Document d = documents.findById(id)
					.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No document "+id));
			String xml = d.getCurrentRevision().getXml();
					
			ADL out = ADLImpl.xmlMode(new URI(uri), xml, headers);
			return out;
		} else if (repository.equals("revisions")) {
			Revision r = revisions.findById(id)
					.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No revision "+id));
			String xml = r.getXml();
			
			ADL out = ADLImpl.xmlMode(new URI(uri), xml, headers);
			return out;
		} else {
			throw new Kite9ProcessingException("/content not available at "+uri);
		}
		
		
	}
	
}
