package com.kite9.k9server.docker;

import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

public class AbstractRestIT extends AbstractDockerIT {

	protected ObjectMapper mapper = new ObjectMapper();
	protected String urlBase = "http://" + getDockerHostName()+ ":8080";

	public AbstractRestIT() {
		super();
	}
	
	protected RestTemplate getRestTemplate() {
		return new RestTemplate(new SimpleClientHttpRequestFactory());
	}

}