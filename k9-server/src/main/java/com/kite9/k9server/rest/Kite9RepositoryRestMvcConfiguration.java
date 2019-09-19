package com.kite9.k9server.rest;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;

import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.webmvc.RepositoryRestHandlerMapping;
import org.springframework.data.rest.webmvc.config.RepositoryRestMvcConfiguration;
import org.springframework.data.rest.webmvc.support.DelegatingHandlerMapping;
import org.springframework.http.MediaType;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.servlet.mvc.condition.ProducesRequestCondition;

import com.kite9.k9server.adl.format.FormatSupplier;

/**
 * Allows REST to handle SVG, PNG and XML media types.
 */
@Configuration
public class Kite9RepositoryRestMvcConfiguration extends RepositoryRestMvcConfiguration {
	
	@Autowired
	private FormatSupplier formatSupplier;
	
	private ApplicationContext context;

	public Kite9RepositoryRestMvcConfiguration(ApplicationContext context, ObjectFactory<ConversionService> conversionService) {
		super(context, conversionService);
		this.context = context;
	}

	@Override
	public DelegatingHandlerMapping restHandlerMapping() {
		DelegatingHandlerMapping out = super.restHandlerMapping();
		RepositoryRestConfiguration repositoryRestConfiguration = repositoryRestConfiguration();
		Map<String, CorsConfiguration> corsConfigurations = repositoryRestConfiguration.getCorsRegistry()
				.getCorsConfigurations();
		
		// create a mapping which supports multiple media types.
		RepositoryRestHandlerMapping repositoryMapping = new RepositoryRestHandlerMapping(resourceMappings(),
				repositoryRestConfiguration, repositories()) {

					@Override
					protected ProducesRequestCondition customize(ProducesRequestCondition condition) {
						if (!condition.isEmpty()) {
							return condition;
						}

						HashSet<String> mediaTypes = new LinkedHashSet<String>();
						
						for (MediaType mt : formatSupplier.getMediaTypes()) {
							mediaTypes.add(mt.toString());
						}
						
						mediaTypes.add(MediaType.APPLICATION_JSON_VALUE);
						mediaTypes.add(repositoryRestConfiguration.getDefaultMediaType().toString());

						return new ProducesRequestCondition(mediaTypes.toArray(new String[mediaTypes.size()]));
					}

		};
		
		repositoryMapping.setJpaHelper(jpaHelper());
		repositoryMapping.setApplicationContext(context);
		repositoryMapping.setCorsConfigurations(corsConfigurations);
		repositoryMapping.afterPropertiesSet();
		
		// replace the original
		out.getDelegates().set(0, repositoryMapping);
		return out;
	}

	
	
}
