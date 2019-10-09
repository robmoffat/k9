package com.kite9.k9server.rest;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

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
import org.springframework.hateoas.Link;
import org.springframework.hateoas.config.EnableEntityLinks;
import org.springframework.hateoas.config.EnableHypermediaSupport;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.util.StringUtils;
import org.springframework.web.accept.ContentNegotiationManager;
import org.springframework.web.accept.HeaderContentNegotiationStrategy;
import org.springframework.web.accept.ParameterContentNegotiationStrategy;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.servlet.mvc.condition.ProducesRequestCondition;
import org.w3c.dom.Document;

import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.introspect.Annotated;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.dataformat.xml.JacksonXmlAnnotationIntrospector;
import com.fasterxml.jackson.dataformat.xml.JacksonXmlModule;
import com.kite9.k9server.adl.format.ADLMessageConverter;
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
	
	@Value("${kite9.rest.transform:classpath:/static/public/context/admin/transform.xslt}")
	private String transformResource;
	
	@Autowired
	ResourceLoader resourceLoader;
	
	
	
	@Override
	public void configureJacksonObjectMapper(ObjectMapper objectMapper) {
		RepositoryRestConfigurer.super.configureJacksonObjectMapper(objectMapper);
		JacksonXmlModule module = new JacksonXmlModule() {

			/**
			 * Forces the use of the Kite9 ADL namespace for elements without annotations.
			 */
			@Override
			protected AnnotationIntrospector _constructIntrospector() {
				return new JacksonXmlAnnotationIntrospector(false) {

					@Override
					public String findNamespace(Annotated ann) {
						if (isOutputAsAttribute(ann) == Boolean.TRUE) {
							return "";
						} 
						
						String out = super.findNamespace(ann);
						if (StringUtils.isEmpty(out)) {
							return XMLHelper.KITE9_NAMESPACE;
						} else {
							return out;
						}
					}

					@Override
					public Boolean isOutputAsAttribute(Annotated ann) {
						// link members are always attributes
						if (ann instanceof AnnotatedMember) {
							if (((AnnotatedMember) ann).getDeclaringClass() == Link.class) {
								return true;
							}
						}

						return super.isOutputAsAttribute(ann);
					}
				};
			}
		};
		objectMapper.registerModule(module);
	}

	@Override
	public void configureHttpMessageConverters(List<HttpMessageConverter<?>> messageConverters) {
		RepositoryRestConfigurer.super.configureHttpMessageConverters(messageConverters);
		messageConverters.add(0, new HateoasADLHttpMessageConverter(repositoryRestMvcConfiguration.objectMapper(), formatSupplier, transformResource, resourceLoader));	
		messageConverters.add(1, new ADLMessageConverter(formatSupplier));
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
		
		// allows us to use format=png or something in the request url
		ContentNegotiationManager cnm = new ContentNegotiationManager(
				new ParameterContentNegotiationStrategy(formatSupplier.getMediaTypeMap()),
				new HeaderContentNegotiationStrategy());

		
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

				return new ProducesRequestCondition(mediaTypes.toArray(new String[mediaTypes.size()]), null, cnm);
			}

		};

		repositoryMapping.setJpaHelper(jpaHelper);
		repositoryMapping.setApplicationContext(context);
		repositoryMapping.setCorsConfigurations(corsConfigurations);
		repositoryMapping.setContentNegotiationManager(cnm);
		repositoryMapping.afterPropertiesSet();
		
		
		for (int i = 0; i < dhm.getDelegates().size(); i++) {
			if (dhm.getDelegates().get(i) instanceof RepositoryRestHandlerMapping) {
				dhm.getDelegates().set(i, repositoryMapping);
			}
		}
		
		return repositoryMapping;
	}
}
