package com.kite9.k9server.rest;

import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.kite9.diagram.dom.XMLHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.repository.support.Repositories;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.core.mapping.RepositoryResourceMappings;
import org.springframework.data.rest.webmvc.RepositoryRestHandlerMapping;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurer;
import org.springframework.data.rest.webmvc.config.RepositoryRestMvcConfiguration;
import org.springframework.data.rest.webmvc.support.DelegatingHandlerMapping;
import org.springframework.data.rest.webmvc.support.JpaHelper;
import org.springframework.hateoas.config.EnableEntityLinks;
import org.springframework.hateoas.config.EnableHypermediaSupport;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.util.StringUtils;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.servlet.mvc.condition.ProducesRequestCondition;

import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.Module.SetupContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.introspect.Annotated;
import com.fasterxml.jackson.dataformat.xml.JacksonXmlAnnotationIntrospector;
import com.fasterxml.jackson.dataformat.xml.JacksonXmlModule;
import com.kite9.k9server.adl.format.FormatSupplier;

@Configuration
@EnableEntityLinks
@EnableHypermediaSupport(type=EnableHypermediaSupport.HypermediaType.HAL)
public class RestDataConfig implements RepositoryRestConfigurer {

	public static final String REST_API_BASE = "/api";

	@Override
	public void configureRepositoryRestConfiguration(RepositoryRestConfiguration config) {
		config.setBasePath(REST_API_BASE);
	}
	
	@Autowired
	FormatSupplier formatSupplier;
	
	@Autowired
	ApplicationContext context;
	
	@Autowired
	RepositoryRestMvcConfiguration repositoryRestMvcConfiguration;
	
	@Value("${kite9.rest.template:classpath:/static/public/context/admin/resource.xml}")
	private String templateResource;
	
	@Autowired
	ResourceLoader resourceLoader;
	
	
	
	@Override
	public void configureJacksonObjectMapper(ObjectMapper objectMapper) {
		RepositoryRestConfigurer.super.configureJacksonObjectMapper(objectMapper);
		objectMapper.registerModule(new JacksonXmlModule() {

			/**
			 * Forces the use of the Kite9 ADL namespace for elements without annotations.
			 */
			@Override
			protected AnnotationIntrospector _constructIntrospector() {
				return new JacksonXmlAnnotationIntrospector(true) {

					@Override
					public String findNamespace(Annotated ann) {
						String out = super.findNamespace(ann);
						if (StringUtils.isEmpty(out)) {
							return XMLHelper.KITE9_NAMESPACE;
						} else {
							return out;
						}
					}
					
				};
			}

			@Override
			public void setupModule(SetupContext context) {
				super.setupModule(context);
				
				// add special HATEOAS deserializers
				
			}
			
			
			
		});
	}

	@Override
	public void configureHttpMessageConverters(List<HttpMessageConverter<?>> messageConverters) {
		try {
			RepositoryRestConfigurer.super.configureHttpMessageConverters(messageConverters);
			String template = IOUtils.toString(resourceLoader.getResource(templateResource).getInputStream(), "UTF-8");
			messageConverters.add(0, new HateoasADLHttpMessageConverter(repositoryRestMvcConfiguration.objectMapper(), formatSupplier, template));	
		} catch (IOException e) {
			throw new RuntimeException("Couldn't find template: ", e);
		}
	}
	
	/**
	 * Creates a handler that allows all the {@link FormatSupplier} media types to be supported by 
	 * REST.
	 */
	@Bean
	public RepositoryRestHandlerMapping createHandlerMapping(RepositoryRestConfiguration config, 
			DelegatingHandlerMapping dhm, RepositoryResourceMappings rrm, 
			Repositories repositories, JpaHelper jpaHelper) {
		Map<String, CorsConfiguration> corsConfigurations = config.getCorsRegistry().getCorsConfigurations();
		
		// create a mapping which supports multiple media types.
		RepositoryRestHandlerMapping repositoryMapping = new RepositoryRestHandlerMapping(rrm, config, repositories) {

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
				mediaTypes.add(config.getDefaultMediaType().toString());

				return new ProducesRequestCondition(mediaTypes.toArray(new String[mediaTypes.size()]));
			}

		};

		repositoryMapping.setJpaHelper(jpaHelper);
		repositoryMapping.setApplicationContext(context);
		repositoryMapping.setCorsConfigurations(corsConfigurations);
		repositoryMapping.afterPropertiesSet();
		
		
		for (int i = 0; i < dhm.getDelegates().size(); i++) {
			if (dhm.getDelegates().get(i) instanceof RepositoryRestHandlerMapping) {
				dhm.getDelegates().set(i, repositoryMapping);
			}
		}
		
		return repositoryMapping;
	}
}
