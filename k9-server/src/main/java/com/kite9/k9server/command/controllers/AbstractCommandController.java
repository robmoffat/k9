package com.kite9.k9server.command.controllers;

import java.net.URI;
import java.util.List;

import org.kite9.framework.logging.Kite9Log;
import org.kite9.framework.logging.Logable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.support.DefaultFormattingConversionService;
import org.springframework.http.HttpHeaders;

import com.kite9.k9server.adl.holder.ADL;
import com.kite9.k9server.command.Command;
import com.kite9.k9server.command.ContextCommand;
import com.kite9.k9server.command.XMLCommand;
import com.kite9.k9server.domain.entity.RestEntity;
import com.kite9.k9server.domain.github.GitHubAPIFactory;

public abstract class AbstractCommandController implements Logable {

	Kite9Log log = new Kite9Log(this);
	
	@Autowired
	GitHubAPIFactory githubApiFactory;
	
	@Autowired
	DefaultFormattingConversionService conversionService;
			
	public AbstractCommandController() {
		super();
	}

	@SuppressWarnings({ "rawtypes" })
	public Object performSteps(List<Command> steps, Object input, RestEntity context, HttpHeaders headers, URI url) throws Exception {
		checkDomainAccess(context, url);
		for (Command command : steps) {
			if (command instanceof XMLCommand) {
				((XMLCommand) command).setOn((ADL) input);
			}
			
			if (command instanceof ContextCommand) {
				((ContextCommand)command).setCommandContext(context, url, headers);
			}
			
//			if (command instanceof RepoCommand) {
//				((RepoCommand)command).setRepositories(repositories);
//			}
			
//			if (command instanceof SubjectCommand) {
//				RestEntity subjectEntity = getSubjectEntity(((SubjectCommand) command).getSubjectUrl(), context);
//				((SubjectCommand)command).setSubjectEntity(subjectEntity);
//				if (subjectEntity != context) {
//					checkDomainAccess(subjectEntity, url);
//				}
//			}
		
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
/*	protected RestEntity getSubjectEntity(String subjectUrl, RestEntity context) throws Kite9ProcessingException {
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
	} */

	@Override
	public String getPrefix() {
		return "SCC ";
	}

	@Override
	public boolean isLoggingEnabled() {
		return true;
	}

	protected void checkDomainAccess(RestEntity<?> d, URI url) {
		
		// do something here.
		
	}
}