package com.kite9.k9server.command.controllers;

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
import org.springframework.hateoas.EntityLinks;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.kite9.k9server.adl.holder.ADL;
import com.kite9.k9server.command.Command;
import com.kite9.k9server.command.ContextCommand;
import com.kite9.k9server.command.RepoCommand;
import com.kite9.k9server.command.SubjectCommand;
import com.kite9.k9server.command.XMLCommand;
import com.kite9.k9server.domain.entity.RestEntity;
import com.kite9.k9server.domain.entity.RestEntityRepository;
import com.kite9.k9server.domain.entity.Secured;

public abstract class AbstractCommandController implements Logable, InitializingBean {

	Kite9Log log = new Kite9Log(this);

	@Autowired
	Repositories repositories;
	
	@Autowired
	EntityLinks entityLinks;
	
	@Autowired
	ResourceMappings mappings;
	
	protected Map<Path, RestEntityRepository<?>> repoMap;
		
	public AbstractCommandController() {
		super();
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Transactional
	public Object performSteps(List<Command> steps, Object input, RestEntity context, HttpHeaders headers, URI url) {
		checkDomainAccess(context, url);
		for (Command command : steps) {
			if (command instanceof XMLCommand) {
				((XMLCommand) command).setOn((ADL) input);
			}
			
			if (command instanceof ContextCommand) {
				((ContextCommand)command).setCommandContext(context, url, headers);
			}
			
			if (command instanceof RepoCommand) {
				((RepoCommand)command).setRepositories(repositories);
			}
			
			if (command instanceof SubjectCommand) {
				((SubjectCommand)command).setSubjectEntity(getSubjectEntity(((SubjectCommand) command).getSubjectUrl(), context));
			}
		
			input = command.applyCommand();
		}
		return input;
	}

	protected RestEntity getSubjectEntity(String subjectUrl, RestEntity context) {
		System.out.println("hello");
		
		if (subjectUrl == null) {
			return context;
		} else {
			// we have a specific subject.
		}
		
		return null;
	}

	@Override
	public String getPrefix() {
		return "SCC ";
	}

	@Override
	public boolean isLoggingEnabled() {
		return true;
	}

	protected void checkDomainAccess(RestEntity d, URI url) {
		if (d instanceof Secured) {
			if (!((Secured) d).checkWrite()) {
				throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "No write access for "+url);
			}
		}
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