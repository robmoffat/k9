package com.kite9.k9server.command.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kite9.framework.logging.Logable;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.Repository;
import org.springframework.data.rest.core.Path;
import org.springframework.data.rest.core.mapping.ResourceMappings;
import org.springframework.data.rest.core.mapping.ResourceMetadata;
import org.springframework.data.rest.webmvc.PersistentEntityResourceAssembler;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.server.ResponseStatusException;

import com.kite9.k9server.adl.format.media.Kite9MediaTypes;
import com.kite9.k9server.command.Command;
import com.kite9.k9server.command.CommandException;
import com.kite9.k9server.domain.entity.RestEntity;
import com.kite9.k9server.domain.entity.RestEntityRepository;
import com.kite9.k9server.domain.user.UserRepository;

/**
 * Accepts commands to the system in order to modify domain objects (resources).
 * 
 * @author robmoffat
 *
 */
@RepositoryRestController 
public class ResourceCommandController extends AbstractCommandController implements Logable, InitializingBean {
	
	@Autowired
	UserRepository userRepository;
	
	@Autowired
	ResourceMappings mappings;
	
	private Map<Path, RestEntityRepository<?>> repoMap;
	
	/**
	 * This is used for applying commands to domain objects.
	 */
	@RequestMapping(method={RequestMethod.POST}, 
		path= {"/{repository}/{id}",
				"/{repository}"}, 
		consumes= {MediaType.APPLICATION_JSON_VALUE},
		produces= {
			MediaTypes.HAL_JSON_VALUE, 
			Kite9MediaTypes.ADL_SVG_VALUE, 
			Kite9MediaTypes.SVG_VALUE
		}) 
	@ResponseBody
	public Object applyCommandOnResource (
				RequestEntity<List<Command>> req,
				@PathVariable(required=false, name="repository") String repository,
				@PathVariable(required=false, name="id") Long id,
				PersistentEntityResourceAssembler assembler) throws CommandException {
		
		RestEntity ri = getEntity(repository, id);
				
		try {
			Object out = performSteps(req.getBody(), null, ri, req.getHeaders(), req.getUrl());
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

	protected RestEntity getEntity(String repository, Long id) {
		if ((repository == null) || (id == null)) {
			return null;
		}
		
		for (Path p : repoMap.keySet()) {
			if (p.matches(repository)) {
				RestEntityRepository<?> repo = p != null ? repoMap.get(p) : null;
				RestEntity ri = ((id != null) && (repo != null)) ? repo.findById(id).orElse(null) : null;
				return ri;
			}
		}
		
		throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Couldn't find domain object");
	}

	@Override
	public String getPrefix() {
		return "RCC ";
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
