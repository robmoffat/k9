package com.kite9.k9server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;

@SpringBootApplication
@EnableAuthorizationServer
public class Kite9ServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(Kite9ServerApplication.class, args);
	}
}
