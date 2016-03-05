package com.kite9.k9server.domain;


import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.client.RestTemplate;

import com.kite9.k9server.Kite9ServerApplication;

@IntegrationTest
@ActiveProfiles("dev")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Kite9ServerApplication.class)
@WebAppConfiguration
public class TestPreloadedProjectsByRest {

	RestTemplate restTemplate = new RestTemplate(new SimpleClientHttpRequestFactory());
	
	@Test
	public void testProjectIsReturned() {
		Project p = restTemplate.getForObject("http://localhost:8080/api/projects/1", Project.class);
		Assert.assertEquals("First Project", p.getTitle());
	}
	
}
