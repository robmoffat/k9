package com.kite9.k9server.rest;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurerAdapter;
import org.springframework.hateoas.config.EnableEntityLinks;
import org.springframework.hateoas.config.EnableHypermediaSupport;

@Configuration
@EnableEntityLinks
@EnableHypermediaSupport(type=EnableHypermediaSupport.HypermediaType.HAL)
public class RestDataConfig extends RepositoryRestConfigurerAdapter {

	public static final String REST_API_BASE = "/api";

	@Override
	public void configureRepositoryRestConfiguration(RepositoryRestConfiguration config) {
		super.configureRepositoryRestConfiguration(config);
		config.setBasePath(REST_API_BASE);
	}
}
