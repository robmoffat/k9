package com.kite9.k9server.web;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.kite9.k9server.adl.format.media.MediaTypes;

import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SwaggerConfig {

	public static final Contact DEFAULT_CONTACT = new Contact(
		      "Rob Moffat", "http://www.robmoff.at", "rob@kite9.com");
		  
		  public static final ApiInfo DEFAULT_API_INFO = new ApiInfo(
		      "Kite9 API", "Kite9 REST API", "1.0",
		      "urn:tos", DEFAULT_CONTACT, 
		      "Apache 2.0", "http://www.apache.org/licenses/LICENSE-2.0", Collections.emptyList());

		  private static final Set<String> DEFAULT_CONSUMES = 
		      new HashSet<String>(Arrays.asList("application/json"));
		  
		  private static final Set<String> DEFAULT_PRODUCES = 
			      new HashSet<String>(Arrays.asList("application/json",
			          "application/xml", MediaTypes.ADL_SVG_VALUE, MediaTypes.SVG_VALUE));

		  @Bean
		  public Docket api() {
		    return new Docket(DocumentationType.SWAGGER_2)
		        .apiInfo(DEFAULT_API_INFO)
		        .produces(DEFAULT_PRODUCES)
		        .consumes(DEFAULT_CONSUMES);
		  }

}
