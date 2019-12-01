package com.kite9.k9server.command.controllers;

import java.lang.reflect.Field;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kite9.framework.common.Kite9ProcessingException;
import org.kite9.framework.logging.Kite9Log;
import org.kite9.framework.logging.Logable;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.support.Repositories;
import org.springframework.data.rest.core.Path;
import org.springframework.data.rest.core.UriToEntityConverter;
import org.springframework.data.rest.core.mapping.ResourceMappings;
import org.springframework.data.rest.core.mapping.ResourceMetadata;
import org.springframework.format.support.DefaultFormattingConversionService;
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
	
	@Autowired
	DefaultFormattingConversionService conversionService;
	
	private Map<String, RestEntityRepository<?>> repoMap;
		
	public AbstractCommandController() {
		super();
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Object performSteps(List<Command> steps, Object input, RestEntity context, HttpHeaders headers, URI url) throws Exception {
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
				RestEntity subjectEntity = getSubjectEntity(((SubjectCommand) command).getSubjectUrl(), context);
				((SubjectCommand)command).setSubjectEntity(subjectEntity);
				if (subjectEntity != context) {
					checkDomainAccess(subjectEntity, url);
				}
			}
		
			input = command.applyCommand();
		}
		return input;
	}

	/**
	 * There should be a better way to do this in spring-data-rest, but at the time of writing, 
	 * it appears that all the existing methods are insufficient.  
	 * 
	 * Specifically, {@link UriToEntityConverter} doesn't handle properties, and you 
	 * can't override it without creating a cycle.
	 */
	protected RestEntity getSubjectEntity(String subjectUrl, RestEntity context) throws Kite9ProcessingException {
		try {
			System.out.println("hello");
			
			if (subjectUrl != null) {
				List<String> parts = Arrays.asList(subjectUrl.split("/"));
				int api = parts.indexOf("api");
				if (api == -1) {
					throw new URISyntaxException(subjectUrl, "was expecting /api");
				} else {
					parts = parts.subList(api+1, parts.size());
				}
				
				if ((parts.size() < 2) || (parts.size() > 4)) {
					throw new URISyntaxException(subjectUrl, "was expecting 2 or 3 parts");
				}
					
				RestEntity subject = null;
				
				String repo = parts.get(0);
				RestEntityRepository<?> rer = repoMap.get("/"+repo);
				Long id = conversionService.convert(parts.get(1), Long.class);
				subject = rer.findById(id).orElseThrow();
					
				if (parts.size() == 3) {
					String field = parts.get(2);
					Field f = subject.getClass().getDeclaredField(field);
					f.setAccessible(true);
					subject = (RestEntity) f.get(subject);
				}
			
				return subject;
			} else {
				return context;
			}
		} catch (Exception e) {
			throw new Kite9ProcessingException("Couldn't do getSubjectEntity", e);
		}
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
				repoMap.put(p.toString(), (RestEntityRepository<?>) r);
			}
		}
	}
}