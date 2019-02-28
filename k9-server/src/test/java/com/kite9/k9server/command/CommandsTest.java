package com.kite9.k9server.command;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Collections;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.kite9.framework.common.TestingHelp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.MailSender;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.StreamUtils;

import com.kite9.k9server.XMLCompare;
import com.kite9.k9server.adl.holder.ADL;
import com.kite9.k9server.adl.holder.ADLImpl;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CommandsTest {
	
	public static final String END_SVG_DOCUMENT = "</svg:svg>";

	public static final String NS = " xmlns=\"http://www.kite9.org/schema/adl\"\n  xmlns:svg='http://www.w3.org/2000/svg' ";

	public static final String START_SVG_DOCUMENT = "<svg:svg xmlns:xlink='http://www.w3.org/1999/xlink' " + NS + ">";
		
	@MockBean
	MailSender mailSender;
	
	@Autowired
	CommandController commandController;
	
	public ADL getInitialADL() throws IOException {
		String xml = StreamUtils.copyToString(this.getClass().getResourceAsStream("/commands/test_command1.xml"), Charset.forName("UTF-8"));
		ADL adl = new ADLImpl(xml, "someuri");
		return adl;
	}
	

	@Test
	public void testSetXMLCommand() throws CommandException, IOException {
		ADL in = getInitialADL();

		
		SetXML setXML = new SetXML();
		setXML.newState =  START_SVG_DOCUMENT+"<glyph id=\"two\"><stereo id=\"two-stereo\" /><label id=\"two-label\" /></glyph>"+END_SVG_DOCUMENT;
		setXML.fragmentId="two";
		
		ADL out = commandController.applyCommand(Collections.singletonList(setXML), in);
		
		String result = out.getAsXMLString();
		TestingHelp.writeOutput(this.getClass(), "testCommandLifecycle", "2.xml", result);
		String expected2 = StreamUtils.copyToString(this.getClass().getResourceAsStream("/commands/test_command2.xml"), Charset.forName("UTF-8"));
		XMLCompare.compareXML(expected2, result);
	}
	
	@Test
	public void testMoveCommand() throws CommandException, IOException {
		ADL in = getInitialADL();

		Move move = new Move();
		move.moveId = "two-stereo";
		move.fragmentId = "two";
		move.beforefragmentId = "two-label";
		
		ADL out = commandController.applyCommand(Collections.singletonList(move), in);
		String result = out.getAsXMLString();
		TestingHelp.writeOutput(this.getClass(), "testCommandLifecycle", "3.xml", result);
		String expected3 = StreamUtils.copyToString(this.getClass().getResourceAsStream("/commands/test_command3.xml"), Charset.forName("UTF-8"));
		XMLCompare.compareXML(expected3, result);
	}
	
	@Test
	public void testDeleteCommand() throws CommandException, IOException {
		ADL in = getInitialADL();

		Delete delete = new Delete();
		delete.fragmentId = "two";
		
		ADL out = commandController.applyCommand(Collections.singletonList(delete), in);
		String result = out.getAsXMLString();
		TestingHelp.writeOutput(this.getClass(), "testCommandLifecycle", "after_delete.xml", result);
		String expected4 = StreamUtils.copyToString(this.getClass().getResourceAsStream("/commands/after_delete.xml"), Charset.forName("UTF-8"));
		XMLCompare.compareXML(expected4, result);
	}
	
}
