package com.kite9.k9server.docker;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import com.kite9.k9server.domain.User;

public class RestUserIT extends AbstractRestIT {

	@Test
	public void testCreateUser() {
		Map<String, String> vars = new HashMap<>();
		String username = "Joe Bloggs";
		String password = "Elephant";
		String email = "joe@example.com";
		
		String url = urlBase + "/api/users/create?username="+username+"&password="+password+"&email="+email;
		
		ResponseEntity<User> uOut = restTemplate.getForEntity(url, User.class, vars);
		
		User u = uOut.getBody();
		Assert.assertEquals(username, u.getUsername());
		Assert.assertNotNull(u.getApi());
	}
}
