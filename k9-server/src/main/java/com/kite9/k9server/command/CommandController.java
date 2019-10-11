package com.kite9.k9server.command;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kite9.framework.logging.Kite9Log;
import org.kite9.framework.logging.Logable;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.support.Repositories;
import org.springframework.data.rest.core.Path;
import org.springframework.data.rest.core.mapping.ResourceMappings;
import org.springframework.data.rest.core.mapping.ResourceMetadata;
import org.springframework.data.rest.webmvc.BasePathAwareController;
import org.springframework.hateoas.EntityLinks;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.server.ResponseStatusException;

import com.kite9.k9server.adl.holder.ADL;
import com.kite9.k9server.adl.holder.ADLImpl;
import com.kite9.k9server.command.domain.DomainCommand;
import com.kite9.k9server.command.xml.XMLCommand;
import com.kite9.k9server.domain.RestEntity;
import com.kite9.k9server.domain.RestEntityRepository;
import com.kite9.k9server.domain.Secured;
import com.kite9.k9server.domain.SecuredCrudRepository;
import com.kite9.k9server.domain.document.Document;
import com.kite9.k9server.domain.document.DocumentRepository;
import com.kite9.k9server.domain.rels.ChangeResourceProcessor;
import com.kite9.k9server.domain.rels.ContentResourceProcessor;
import com.kite9.k9server.domain.revision.Revision;
import com.kite9.k9server.domain.revision.RevisionRepository;
import com.kite9.k9server.domain.user.User;
import com.kite9.k9server.domain.user.UserRepository;

/**
 * Accepts commands to the system in order to modify XML.  Contents are returned back in whatever format is
 * requested.
 * 
 * @author robmoffat
 *
 */
@BasePathAwareController
public class CommandController implements Logable, InitializingBean {
	
	private Kite9Log log = new Kite9Log(this);
	
	@Autowired
	Repositories repositories;
	
	@Autowired
	UserRepository userRepository;
	
	@Autowired
	RevisionRepository revisionRepository;
	
	@Autowired
	DocumentRepository documentRepository;
	
	@Autowired
	EntityLinks entityLinks;
		
	@Autowired
	ResourceMappings mappings;
	
	private Map<Path, RestEntityRepository<?>> repoMap;
	
	@RequestMapping(method={RequestMethod.POST}, path="/command/v1", consumes= {MediaType.APPLICATION_JSON_VALUE})
	public @ResponseBody ADL applyCommandOnStatic (
			RequestEntity<List<Command>> req,
			@RequestParam(required=true, name="on") String sourceUri) throws Exception {
		
		URI uri = new URI(sourceUri);
		URI base = req.getUrl();
		uri = base.resolve(uri);
		ADL input = new ADLImpl(uri, req.getHeaders());
		
		return performXMLCommands(req.getBody(), input, null, req.getHeaders(), uri);
	}
	
	/**
	 * This is used for applying commands to domain objects.
	 */
	@Transactional
	@RequestMapping(method={RequestMethod.POST}, path="/{repository}/{id}/change", consumes= {MediaType.APPLICATION_JSON_VALUE}) 
	public @ResponseBody Object applyCommandOnResource (
				RequestEntity<List<Command>> req,
				@PathVariable(required=true, name="repository") String repository,
				@PathVariable(required=true, name="id") Long id) throws Exception {
		
		for (Path p : repoMap.keySet()) {
			if (p.matches(repository)) {
				RestEntityRepository<?> repo = repoMap.get(p);
				RestEntity ri = repo.findById(id).orElse(null);
				Class<? extends Command> type = identifyCommandTypes(req.getBody());
				
				if ((type == XMLCommand.class) && (ri instanceof Document)) {
					ADL input = new ADLImpl(((Document) ri).getCurrentRevision().getXml(), req.getUrl(), req.getHeaders());
					return performXMLCommands(req.getBody(), input, ri, req.getHeaders(), req.getUrl());
				} else if (type == DomainCommand.class) {
					return performDomainCommands(req.getBody(), ri, req.getHeaders(), req.getUrl());
				} else {
					throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Couldn't process command steps");
				}
			}
		}
		
		throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Couldn't find domain object");
		
	}
	
	protected RestEntity performDomainCommands(List<Command> steps, RestEntity context, HttpHeaders headers, URI url) {
		checkDomainAccess(context, url);
		performSteps(steps, null, context, headers, url);
		return context;
	}

	protected ADL performXMLCommands(List<Command> steps, ADL input, RestEntity context, HttpHeaders headers, URI url) {
		Document d = null;
		
		if (log.go()) {
			log.send("Before: " + input.getAsXMLString());
		}
		
		d = checkDocumentAccess(context, url, d);
		input = performSteps(steps, input, context, headers, url);
		checkRenderable(input);
		
		if (d != null) {
			// create the new revision
			Revision rNew = createNewRevisionOnDocument(input, d);
			addDocumentMeta(input, rNew);
		}	
		
		if (log.go()) {
			log.send("After: " + input.getAsXMLString());
		}
		
		return input;
	}

	protected Revision createNewRevisionOnDocument(ADL input, Document d) {
		String username = SecurityContextHolder.getContext().getAuthentication().getName();
		User u = userRepository.findByUsername(username);
		
		Revision rNew = new Revision();
		rNew.setAuthor(u);
		rNew.setDocument(d);
		rNew.setXml(input.getAsXMLString());
		revisionRepository.save(rNew);
		return rNew;
	}

	protected void checkRenderable(ADL input) {
		input.getSVGRepresentation();
	}

	/**
	 * Since we are in a document, add some meta-data about revisions, and the redo situation.
	 */
	private ADL addDocumentMeta(ADL adl, Revision r) {
		adl.setMeta("redo", ""+(r.getNextRevision() != null));
		adl.setMeta("undo", ""+(r.getPreviousRevision() != null));
		adl.setMeta("revision", entityLinks.linkFor(Revision.class).slash(r.getId()).toString());
		String documentUrl = entityLinks.linkFor(Document.class).slash(r.getDocument().getId()).toString();
		adl.setMeta(ChangeResourceProcessor.CHANGE_REL, documentUrl+ChangeResourceProcessor.CHANGE_URL);
		adl.setMeta(ContentResourceProcessor.CONTENT_REL, documentUrl+ContentResourceProcessor.CONTENT_URL);
		adl.setMeta("author", r.getAuthor().getUsername());
		return adl;
	}

	protected Document checkDocumentAccess(RestEntity context, URI url, Document d) {
		if (context != null) {
			if (!(context instanceof Document)) {
				throw new CommandException("XMLCommands must apply to a document "+url, null);
			}

			d = (Document) context;
			checkDomainAccess(d, url);
		}
		return d;
	}
	
	protected void checkDomainAccess(RestEntity d, URI url) {
		if (d instanceof Secured) {
			if (!((Secured) d).checkWrite()) {
				throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "No write access for "+url);
			}
		}
	}

	protected ADL performSteps(List<Command> steps, ADL input, RestEntity context, HttpHeaders headers, URI url) {
		for (Command command : steps) {
			if (command instanceof XMLCommand) {
				((XMLCommand) command).setOn(input);
			}
			
			if (command instanceof DomainCommand) {
				Object repo = repositories.getRepositoryFor(context.getClass()).orElseThrow(() -> new CommandException("No repository for "+context.getClass(), command));
				((DomainCommand)command).setCommandContext((SecuredCrudRepository) repo, context, url, headers);
			}
		
			input = command.applyCommand();
		}
		return input;
	}

	private Class<? extends Command> identifyCommandTypes(List<Command> steps) {
		Class<? extends Command> out = null;
		for (Command command : steps) {
			if (out == null) {
				if (command instanceof XMLCommand) {
					out = XMLCommand.class;
//				} else if (command instanceof RevisionCommand) {
//					out = RevisionCommand.class;
				} else if (command instanceof DomainCommand) {
					out = DomainCommand.class;
				} else {
					throw new CommandException("Can't identity type of command", command);
				}
			} else {
				if (!out.isAssignableFrom(command.getClass())) {
					throw new CommandException("Command "+command+" should also be an instance of "+out.getName(), command);
				}
			}
		}
		
		return out;
	}

	@Override
	public String getPrefix() {
		return "CC  ";
	}

	@Override
	public boolean isLoggingEnabled() {
		return true;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		repoMap = new HashMap<>();
		
		for (Class<?> domainType : repositories) {
			ResourceMetadata mapping = mappings.getMetadataFor(domainType);
			Path p = mapping.getPath();
			Repository<?, ?> r = (Repository<?, ?>) repositories.getRepositoryFor(domainType).get();
			if (r instanceof RestEntityRepository<?>) {
				repoMap.put(p, (RestEntityRepository<?>) r);
			}
		}
	}
}
