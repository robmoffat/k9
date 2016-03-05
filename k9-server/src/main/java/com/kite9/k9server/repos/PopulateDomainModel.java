package com.kite9.k9server.repos;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.kite9.k9server.domain.Project;

/**
 * Populates the in-memory database with some test details.  
 * Only used in the dev profile, so should be tested with that.
 * 
 * @author robmoffat
 *
 */
@Component
@Profile("dev")
public class PopulateDomainModel implements CommandLineRunner {

	@Autowired
	ProjectRepository projectRepository;
	
	
	@Override
	public void run(String... arg0) throws Exception {
		projectRepository.save(new Project("First Project", "Lorem Ipsum delor sit amet", "firstp"));
		projectRepository.save(new Project("Second Project", "Lorem Ipsum delor sit amet 2", "secondo"));
	}

}
