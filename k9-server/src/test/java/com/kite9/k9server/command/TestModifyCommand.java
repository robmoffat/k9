package com.kite9.k9server.command;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;

import org.junit.Assert;
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

import com.kite9.k9server.XMLCompare;
import com.kite9.k9server.adl.holder.ADL;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TestModifyCommand {
	
	public static final String END_SVG_DOCUMENT = "</svg:svg>";

	public static final String NS = " xmlns=\"http://www.kite9.org/schema/adl\"\n  xmlns:svg='http://www.w3.org/2000/svg' ";

	public static final String START_SVG_DOCUMENT = "<svg:svg xmlns:xlink='http://www.w3.org/1999/xlink' " + NS + ">";
	
	public static final String EMPTY_DOCUMENT = START_SVG_DOCUMENT + END_SVG_DOCUMENT;
	
	@MockBean
	MailSender mailSender;
	
	@Autowired
	StepsCommandController commandController;
	
	
	public String getURI() {
		URL u = this.getClass().getResource("/designer-server.css");
		return u.toString();
	}
	
	@Test
	public void testNoStepCommand() throws Exception {
		
	}
	
	@Test
	public void testCommandLifecycle() throws Exception {

		ADL adl = testCreateCommand();
		
		// step 2: insert content (add a stereotype)
		String before = START_SVG_DOCUMENT+ "<glyph id=\"two\"><label id=\"two-label\" /></glyph>" + END_SVG_DOCUMENT;
		String after = START_SVG_DOCUMENT+"<glyph id=\"two\"><stereo id=\"two-stereo\" /><label id=\"two-label\" /></glyph>"+END_SVG_DOCUMENT;
		adl = testModifyCommand(adl, before, after);
		
		// step 2b: try an invalid command (state changed already)
		try {
			adl = testModifyCommand(adl, before, after);
			Assert.fail();
		} catch (CommandException ce) {
			// good
		}
		
		adl = testMoveCommand(adl, after);
		adl = testDeleteCommand(adl);
	}
	
	public ADL testDeleteCommand(ADL in) throws CommandException, IOException {
		String oldState = START_SVG_DOCUMENT + "<glyph id=\"two\"><label id=\"two-label\">Two</label><stereo id=\"two-stereo\"/></glyph>" + END_SVG_DOCUMENT;
		StepsCommand delete = new StepsCommand(in, new Step(StepType.DELETE, null, null, "two", oldState, null));
		ADL out = commandController.applyCommand(delete);
		String result = out.getAsXMLString();
		TestingHelp.writeOutput(this.getClass(), "testCommandLifecycle", "4.xml", result);
		String expected4 = StreamUtils.copyToString(this.getClass().getResourceAsStream("/test_command4.xml"), Charset.forName("UTF-8"));
		XMLCompare.compareXML(expected4, result);
		return out;
	}

	public ADL testMoveCommand(ADL in, String before) throws CommandException, IOException {
		StepsCommand move = new StepsCommand(in,  new Step(StepType.MOVE, "two-stereo", "two", "two-label", before, null));
		ADL out = commandController.applyCommand(move);
		String result = out.getAsXMLString();
		TestingHelp.writeOutput(this.getClass(), "testCommandLifecycle", "3.xml", result);
		String expected3 = StreamUtils.copyToString(this.getClass().getResourceAsStream("/test_command3.xml"), Charset.forName("UTF-8"));
		XMLCompare.compareXML(expected3, result);
		return out;
	}

	public ADL testModifyCommand(ADL in, String before, String after) throws CommandException, IOException {
		StepsCommand modify = new StepsCommand(in, new Step(StepType.MODIFY, null, null, "two", before, after));
		ADL out = commandController.applyCommand(modify);
		String result = out.getAsXMLString();
		TestingHelp.writeOutput(this.getClass(), "testCommandLifecycle", "2.xml", result);
		String expected2 = StreamUtils.copyToString(this.getClass().getResourceAsStream("/test_command2.xml"), Charset.forName("UTF-8"));
		XMLCompare.compareXML(expected2, result);
		return out;
	}

	public ADL testCreateCommand() throws IOException, CommandException {
		// step 1: create
		String xml = StreamUtils.copyToString(this.getClass().getResourceAsStream("/test_command1.xml"), Charset.forName("UTF-8"));
		StepsCommand create = new StepsCommand(null, new Step(StepType.CREATE_DOC, null, null, null, null, xml));
		ADL out = commandController.applyCommand(create);
		String result = out.getAsXMLString();
		TestingHelp.writeOutput(this.getClass(), "testCommandLifecycle", "1.xml", result);
		XMLCompare.compareXML(xml, result);
		return out;
	}
	
	
}
