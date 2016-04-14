package com.kite9.k9server.repos;

import java.util.List;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurerAdapter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;

@Configuration
public class RestDataConfig extends RepositoryRestConfigurerAdapter {

	public static final String REST_API_BASE = "/api";

	@Override
	public void configureRepositoryRestConfiguration(RepositoryRestConfiguration config) {
		super.configureRepositoryRestConfiguration(config);
		config.setBasePath(REST_API_BASE);
	}

//	@Override
//	public void configureHttpMessageConverters(List<HttpMessageConverter<?>> messageConverters) {
//		messageConverters.add(new StringHttpMessageConverter()<T>() {
//			
//			
//		})
//		super.configureHttpMessageConverters(messageConverters);
//	}

}
