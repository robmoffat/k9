package com.kite9.k9server.domain;

import java.nio.charset.Charset;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.util.StreamUtils;

import com.kite9.k9server.domain.document.Document;
import com.kite9.k9server.domain.document.DocumentRepository;
import com.kite9.k9server.domain.permission.MemberRepository;
import com.kite9.k9server.domain.project.Project;
import com.kite9.k9server.domain.project.ProjectRepository;
import com.kite9.k9server.domain.revision.Revision;
import com.kite9.k9server.domain.revision.RevisionRepository;
import com.kite9.k9server.domain.user.UserRepository;

/**
 * Populates the in-memory database with some test details.  
 * Only used in the dev profile, so should be tested with that.
 * 
 * @author robmoffat
 *
 */
//@Component
//@Profile(PopulateDomainModel.PROFILE)
public class PopulateDomainModel implements CommandLineRunner {
	
	public static final String PROFILE = "populate";

	@Autowired
	ProjectRepository projectRepository;
	
	@Autowired
	DocumentRepository documentRepository;
	
	@Autowired
	UserRepository userRepository;
	
	@Autowired
	MemberRepository memberRepository;
	
	@Autowired
	RevisionRepository revisionRepository;
	
	@Override
	public void run(String... arg0) throws Exception {
		// add some projects
		Project project1 = new Project("First Project", "Lorem Ipsum delor sit amet");
		Project project2 = new Project("Second Project", "Lorem Ipsum delor sit amet 2");

		projectRepository.save(project1);
		projectRepository.save(project2);
	
		// add some documents to the projects
		Document document1 = new Document("Document 1", "Document Description", project1);
		Document document2 = new Document("Document 2", "Document Description", project1);
		Document document3 = new Document("Document 3", "Document Description", project2);

		documentRepository.save(document1);
		documentRepository.save(document2);
		documentRepository.save(document3);
		
		// add a revision
		Revision r = new Revision();
		r.setDocument(document1);
		r.setAuthor(TEST_USER);
		r.setDateCreated(new Date());
		r.setXml(StreamUtils.copyToString(this.getClass().getResourceAsStream("/static/public/examples/basic/example.xml"), Charset.forName("UTF-8")));
		revisionRepository.save(r);
		document1.setCurrentRevision(r);
		documentRepository.save(document1);
		
		// add a risk-first example
		Revision r2 = new Revision();
		r2.setDocument(document2);
		r2.setAuthor(TEST_USER);
		r2.setDateCreated(new Date());
		r2.setXml(StreamUtils.copyToString(this.getClass().getResourceAsStream("/static/public/examples/risk-first/example.xml"), Charset.forName("UTF-8")));
		revisionRepository.save(r2);
		document2.setCurrentRevision(r2);
		documentRepository.save(document2);
	}

}
