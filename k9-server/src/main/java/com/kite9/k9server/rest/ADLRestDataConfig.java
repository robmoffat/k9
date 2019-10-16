package com.kite9.k9server.rest;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.repository.support.Repositories;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.core.mapping.RepositoryResourceMappings;
import org.springframework.data.rest.core.mapping.ResourceMappings;
import org.springframework.data.rest.webmvc.RepositoryRestHandlerMapping;
import org.springframework.data.rest.webmvc.support.DelegatingHandlerMapping;
import org.springframework.data.rest.webmvc.support.JpaHelper;
import org.springframework.http.MediaType;
import org.springframework.web.accept.ContentNegotiationManager;
import org.springframework.web.accept.HeaderContentNegotiationStrategy;
import org.springframework.web.accept.ParameterContentNegotiationStrategy;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.servlet.mvc.condition.ProducesRequestCondition;

import com.kite9.k9server.adl.format.FormatSupplier;

@Configuration
public class ADLRestDataConfig implements InitializingBean {

	@Autowired
	DelegatingHandlerMapping delegatingHandlerMapping;
	
	@Autowired
	FormatSupplier formatSupplier;
	
	@Autowired
	ApplicationContext context;
	
	@Autowired
	RepositoryRestConfiguration config;
	
	@Autowired
	Repositories repositories;
	
	@Autowired
	RepositoryResourceMappings rrm;
	
	@Autowired
	JpaHelper jpaHelper;
	
	@Override
	public void afterPropertiesSet() throws Exception {
//		delegatingHandlerMapping.getDelegates().add(0, adlRepositoryRestHandlerMapping());
	}		
		
	//@Bean	
	public ADLRepositoryRestHandlerMapping adlRepositoryRestHandlerMapping() {	
		// allows us to request back adl-svg+xml as the response type
		Map<String, CorsConfiguration> corsConfigurations = config.getCorsRegistry().getCorsConfigurations();

		// allows us to use format=png or something in the request url
		ContentNegotiationManager cnm = new ContentNegotiationManager(
				new ParameterContentNegotiationStrategy(formatSupplier.getMediaTypeMap()),
				new HeaderContentNegotiationStrategy());

		// creates a rrhm for the formatSupplier media types.
		ADLRepositoryRestHandlerMapping repositoryMapping = new ADLRepositoryRestHandlerMapping(rrm, config, repositories) {

			@Override
			protected ProducesRequestCondition customize(ProducesRequestCondition condition) {
				if (!condition.isEmpty()) {
					return condition;
				}

				HashSet<String> mediaTypes = new LinkedHashSet<String>();

				for (MediaType mt : formatSupplier.getMediaTypes()) {
					mediaTypes.add(mt.toString());
				}

				return new ProducesRequestCondition(mediaTypes.toArray(new String[mediaTypes.size()]), null, cnm);
			}

		};

		repositoryMapping.setJpaHelper(jpaHelper);
		repositoryMapping.setApplicationContext(context);
		repositoryMapping.setCorsConfigurations(corsConfigurations);
		repositoryMapping.setContentNegotiationManager(cnm);
		repositoryMapping.afterPropertiesSet();
		
		return repositoryMapping;
	}

	static class ADLRepositoryRestHandlerMapping extends RepositoryRestHandlerMapping {

		public ADLRepositoryRestHandlerMapping(ResourceMappings mappings, RepositoryRestConfiguration config, Repositories repositories) {
			super(mappings, config, repositories);
		}
		
		
	}
}
