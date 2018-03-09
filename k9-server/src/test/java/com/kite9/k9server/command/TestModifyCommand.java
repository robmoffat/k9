package com.kite9.k9server.command;

import java.io.IOException;
import java.net.URL;
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

import com.kite9.k9server.domain.document.Document;
import com.kite9.k9server.domain.document.DocumentRepository;
import com.kite9.k9server.domain.project.Project;
import com.kite9.k9server.domain.project.ProjectRepository;
import com.kite9.k9server.domain.revision.Revision;
import com.kite9.k9server.domain.revision.RevisionRepository;
import com.kite9.k9server.domain.user.User;
import com.kite9.k9server.domain.user.UserRepository;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TestModifyCommand {
	
	public static final String END_SVG_DOCUMENT = "</svg:svg>";

	public static final String NS = " xmlns=\"http://www.kite9.org/schema/adl\"\n  xmlns:svg='http://www.w3.org/2000/svg' ";

	public static final String START_SVG_DOCUMENT = "<svg:svg xmlns:xlink='http://www.w3.org/1999/xlink' " + NS + ">";
	
	public static final String EMPTY_DOCUMENT = START_SVG_DOCUMENT + END_SVG_DOCUMENT;
	
	@Autowired
	ProjectRepository projectRepository;
	
	@Autowired
	DocumentRepository documentRepository;
	
	@Autowired
	CommandController commandController;
	
	@Autowired
	UserRepository userRepository;
	
	@Autowired
	RevisionRepository revisionRepository;
	
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
	
	public String getURI() {
		URL u = this.getClass().getResource("/designer-server.css");
		return u.toString();
	}
	
	@Test
	public void testCommandLifecycle() throws Exception {

		Revision out = testCreateCommand();
		
		// step 2: insert content (add a stereotype)
		String before = START_SVG_DOCUMENT+ "<glyph id=\"two\"><label id=\"two-label\" /></glyph>" + END_SVG_DOCUMENT;
		String after = START_SVG_DOCUMENT+"<glyph id=\"two\"><stereo id=\"two-stereo\" /><label id=\"two-label\" /></glyph>"+END_SVG_DOCUMENT;
		out = testModifyCommand(before, after);
		
		// step 2b: try an invalid command (state changed already)
		try {
			out = testModifyCommand(before, after);
			Assert.fail();
		} catch (CommandException ce) {
			// good
		}
		
		out = testMoveCommand(after);
		out = testDeleteCommand();

		// make sure we have the correct number of revisions
		Assert.assertEquals(4, revisionRepository.count());
		
	}
	
	public Revision testDeleteCommand() throws CommandException, IOException {
		Revision out;
		String oldState = START_SVG_DOCUMENT + "<glyph id=\"two\"><label id=\"two-label\">Two</label><stereo id=\"two-stereo\"/></glyph>" + END_SVG_DOCUMENT;
		StepsCommand delete = new StepsCommand(d, u, new Step(StepType.DELETE, null, null, "two", oldState, null));
		out = commandController.applyCommand(delete);
		String result = out.getInputXml();
		TestingHelp.writeOutput(this.getClass(), "testCommandLifecycle", "4.xml", result);
		String expected4 = StreamUtils.copyToString(this.getClass().getResourceAsStream("/test_command4.xml"), Charset.forName("UTF-8"));
		compareXML(expected4, result);
		return out;
	}

	public Revision testMoveCommand(String before) throws CommandException, IOException {
		Revision out;
		StepsCommand move = new StepsCommand(d, u, new Step(StepType.MOVE, "two-stereo", "two", "two-label", before, null));
		out = commandController.applyCommand(move);
		String result = out.getInputXml();
		TestingHelp.writeOutput(this.getClass(), "testCommandLifecycle", "3.xml", result);
		String expected3 = StreamUtils.copyToString(this.getClass().getResourceAsStream("/test_command3.xml"), Charset.forName("UTF-8"));
		compareXML(expected3, result);
		return out;
	}

	public Revision testModifyCommand(String before, String after) throws CommandException, IOException {
		StepsCommand modify = new StepsCommand(d, u, new Step(StepType.MODIFY, null, null, "two", before, after));
		Revision out = commandController.applyCommand(modify);
		String result = out.getInputXml();
		TestingHelp.writeOutput(this.getClass(), "testCommandLifecycle", "2.xml", result);
		String expected2 = StreamUtils.copyToString(this.getClass().getResourceAsStream("/test_command2.xml"), Charset.forName("UTF-8"));
		compareXML(expected2, result);
		return out;
	}

	public Revision testCreateCommand() throws IOException, CommandException {
		// step 1: create
		String xml = StreamUtils.copyToString(this.getClass().getResourceAsStream("/test_command1.xml"), Charset.forName("UTF-8"));
		StepsCommand create = new StepsCommand(d, u, new Step(StepType.CREATE_DOC, null, null, null, null, xml));
		Revision out = commandController.applyCommand(create);
		String result = out.getInputXml();
		TestingHelp.writeOutput(this.getClass(), "testCommandLifecycle", "1.xml", result);
		compareXML(xml, result);
		return out;
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
