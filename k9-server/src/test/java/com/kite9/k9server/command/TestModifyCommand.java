package com.kite9.k9server.command;

import java.nio.charset.Charset;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kite9.framework.common.TestingHelp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.MailSender;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.StreamUtils;
import org.xmlunit.builder.Input;
import org.xmlunit.diff.Comparison;
import org.xmlunit.diff.ComparisonListener;
import org.xmlunit.diff.ComparisonResult;
import org.xmlunit.diff.ComparisonType;
import org.xmlunit.diff.DOMDifferenceEngine;

import com.kite9.k9server.adl.holder.ADL;
import com.kite9.k9server.domain.Document;
import com.kite9.k9server.domain.DocumentRepository;
import com.kite9.k9server.domain.Project;
import com.kite9.k9server.domain.ProjectRepository;
import com.kite9.k9server.domain.User;
import com.kite9.k9server.security.user_repo.UserRepository;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TestModifyCommand {
	
	public static final String EMPTY_DOCUMENT = "<svg:svg xmlns:xlink='http://www.w3.org/1999/xlink' \n" + 
			"  xmlns=\"http://www.kite9.org/schema/adl\"\n" + 
			"  xmlns:svg='http://www.w3.org/2000/svg'></svg:svg>";

	
	@Autowired
	ProjectRepository projectRepository;
	
	@Autowired
	DocumentRepository documentRepository;
	
	@Autowired
	CommandController commandController;
	
	@Autowired
	UserRepository userRepository;
	
	@MockBean
	MailSender mailSender;
	
	private Document d;
	private User u;
	
	@Before
	public void ensureUser() throws Exception {
		if (u == null) {
			User uIn = new User("bob", "abc123","hello@rob.com");
			uIn.setEmailVerified(true);
			u = userRepository.save(uIn);
		}
	}
	
	@Before
	public void ensureDocument() throws Exception {
		if (d == null) {
			Project pIn = new Project("Test Project 2", "Lorem Ipsum 1", "tp2");
			Project pSaved = projectRepository.save(pIn);
			Document dIn = new Document("Some Document", "blah", pSaved);
			d = documentRepository.save(dIn);
		}
	}
	
	@Test
	public void testCommandLifecycle() throws Exception {
		String xml = StreamUtils.copyToString(this.getClass().getResourceAsStream("/test_command1.xml"), Charset.forName("UTF-8"));
		
		StepsCommand create = new StepsCommand(d, u, new Step(StepType.CREATE_DOC, null, null, null, null, xml));
		ADL out = commandController.applyCommand(create);
		String result = out.getAsXMLString();
		TestingHelp.writeOutput(this.getClass(), "testCommandLifecycle", "1.xml", result);
		compareXML(xml, result);
		
		
//		StepsCommand mc = new StepsCommand(d, u, new Step(StepType.MODIFY, null, null, "two-label", 
//				"<label id=\"two-label\">Two</label>",
//				"<label id=\"two-label\">Desmond</label>"));
//		ADL out = commandController.applyCommand(mc);
		
		
		//System.out.println("Change: "+change);
	}
	
	private void compareXML(String a, String b) {
		DOMDifferenceEngine diff = new DOMDifferenceEngine();
		
		
		diff.addDifferenceListener(new ComparisonListener() {
			
	        public void comparisonPerformed(Comparison comparison, ComparisonResult outcome) {
	        		if (comparison.getType() != ComparisonType.XML_ENCODING) {
	        			Assert.fail("found a difference: " + comparison);
	        		}
	        }
	    });
		
		diff.compare(Input.fromString(a).build(), Input.fromString(b).build());
	}
}
