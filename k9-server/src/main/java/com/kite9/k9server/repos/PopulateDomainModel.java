package com.kite9.k9server.repos;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.kite9.k9server.domain.Document;
import com.kite9.k9server.domain.Project;
import com.kite9.k9server.domain.User;
import com.kite9.k9server.security.user_repo.UserRepository;

/**
 * Populates the in-memory database with some test details.  
 * Only used in the dev profile, so should be tested with that.
 * 
 * @author robmoffat
 *
 */
@Component
@Profile("populate")
public class PopulateDomainModel implements CommandLineRunner {
	
	public static final User TEST_USER = new User("test1", "blah", "test1@kite9.com");
	
	static {
		TEST_USER.setApi("test-user-api-key");
	}

	@Autowired
	ProjectRepository projectRepository;
	
	@Autowired
	DocumentRepository documentRepository;
	
	@Autowired
	UserRepository userRepository;
	
	
	@Override
	public void run(String... arg0) throws Exception {
		// add some projects
		Project project1 = new Project("First Project", "Lorem Ipsum delor sit amet", "firstp");
		Project project2 = new Project("Second Project", "Lorem Ipsum delor sit amet 2", "secondo");

		projectRepository.save(project1);
		projectRepository.save(project2);
	
		// add some documents to the projects
		Document document1 = new Document("Document 1", "Document Description", project1);
		Document document2 = new Document("Document 2", "Document Description", project1);
		Document document3 = new Document("Document 3", "Document Description", project2);

		documentRepository.save(document1);
		documentRepository.save(document2);
		documentRepository.save(document3);
		
		// add the test user
		userRepository.save(TEST_USER);
	}

}
