package com.kite9.k9server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories	
public class Kite9ServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(Kite9ServerApplication.class, args);
	}
}
