package com.kite9.k9server.rest;

import java.util.List;

import org.kite9.diagram.dom.XMLHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurer;
import org.springframework.data.rest.webmvc.config.RepositoryRestMvcConfiguration;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.config.EnableHypermediaSupport;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.introspect.Annotated;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.dataformat.xml.JacksonXmlAnnotationIntrospector;
import com.fasterxml.jackson.dataformat.xml.JacksonXmlModule;
import com.kite9.k9server.adl.format.ADLMessageConverter;
import com.kite9.k9server.adl.format.FormatSupplier;

@Configuration
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
		// this prevents exceptions when we are trying to serialize lazy hibernate collections
		objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
	}

	@Override
	public void configureHttpMessageConverters(List<HttpMessageConverter<?>> messageConverters) {
		String changeUri = REST_API_BASE+"/admin";
		RepositoryRestConfigurer.super.configureHttpMessageConverters(messageConverters);
		messageConverters.add(0, new HateoasADLHttpMessageConverter(repositoryRestMvcConfiguration.objectMapper(), formatSupplier, transformResource, resourceLoader, changeUri));	
		messageConverters.add(1, new ADLMessageConverter(formatSupplier));
	}
}
