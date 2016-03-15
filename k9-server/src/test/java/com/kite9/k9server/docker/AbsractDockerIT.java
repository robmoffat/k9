package com.kite9.k9server.docker;

import org.junit.AfterClass;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;

import com.kite9.k9server.Kite9ServerApplication;

public class AbsractDockerIT {
	
	private static ApplicationContext ctx;

	public String getDockerHostName() {
		if (!System.getenv().containsKey("DOCKER_HOST")) {
			if (ctx == null) {
				System.setProperty("spring.profiles.active", "dev");
				ctx = SpringApplication.run(Kite9ServerApplication.class);
			}
			return "localhost";
		} else {
			String name = System.getenv().get("DOCKER_HOST");
			name = name.substring(name.lastIndexOf("/")+1);
			name = name.substring(0, name.lastIndexOf(":"));
			
			return name;
		}
	}

	@AfterClass
	public static void dispose() {
		if (ctx != null)
			SpringApplication.exit(ctx);
	}
}
