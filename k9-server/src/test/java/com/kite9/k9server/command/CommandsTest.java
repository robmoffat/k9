package com.kite9.k9server.command;

import java.net.URI;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Collections;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.kite9.framework.common.TestingHelp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.MailSender;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.StreamUtils;

import com.kite9.k9server.XMLCompare;
import com.kite9.k9server.adl.holder.ADL;
import com.kite9.k9server.adl.holder.ADLImpl;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment=WebEnvironment.DEFINED_PORT)
public class CommandsTest {
	
	public static final String END_SVG_DOCUMENT = "</svg:svg>";

	public static final String NS = " xmlns=\"http://www.kite9.org/schema/adl\"\n  xmlns:svg='http://www.w3.org/2000/svg' ";

	public static final String START_SVG_DOCUMENT = "<svg:svg xmlns:xlink='http://www.w3.org/1999/xlink' " + NS + ">";
		
	@MockBean
	MailSender mailSender;
	
	@Autowired
	CommandController commandController;
	
	public ADL getInitialADL() throws Exception {
		String xml = StreamUtils.copyToString(this.getClass().getResourceAsStream("/commands/test_command1.xml"), Charset.forName("UTF-8"));
		ADL adl = new ADLImpl(xml, new URI("/public/commands/test_command1.xml"), null);
		return adl;
	}
	

	@Test
	public void testReplaceCommand1() throws CommandException, Exception {
		ADL in = getInitialADL();
		
		Replace replace = new Replace();
		replace.fragmentId = "link";
		replace.uriStr= "#one";
		replace.approach = Replace.Approach.SHALLOW;
		replace.keptAttributes = Arrays.asList("rank", "id");
		
		ADL out = commandController.applyCommand(Collections.singletonList(replace), in);
		performSaveAndCheck(out, "replace");

	}
	
	@Test
	public void testReplaceCommand2() throws CommandException, Exception {
		ADL in = getInitialADL();
		
		Replace replace = new Replace();
		replace.fragmentId = "link";
		replace.uriStr= "#one";
		replace.approach = Replace.Approach.DEEP;
		replace.keptAttributes = Arrays.asList("rank", "id");
		
		ADL out = commandController.applyCommand(Collections.singletonList(replace), in);
		performSaveAndCheck(out, "replace-all");

	}
	
	@Test
	public void testReplaceCommand3() throws CommandException, Exception {
		ADL in = getInitialADL();
		
		Replace replace = new Replace();
		replace.fragmentId = "one";
		replace.uriStr= "#link-to";
		replace.approach = Replace.Approach.ATTRIBUTES;
		replace.keptAttributes = Arrays.asList("rank", "id");
		
		ADL out = commandController.applyCommand(Collections.singletonList(replace), in);
		performSaveAndCheck(out, "replace-attr");

	}
	
	@Test
	public void testMoveCommand() throws CommandException, Exception {
		ADL in = getInitialADL();

		Move move = new Move();
		move.moveId = "one-label";
		move.fragmentId = "two";
		move.beforeFragmentId = "two-label";
		move.fragmentHash = "5e9a0d9f2773b0209b111884bae251e26e1c1d88";
		
		ADL out = commandController.applyCommand(Collections.singletonList(move), in);
		performSaveAndCheck(out, "move");
	}
	
	@Test
	public void testDeleteCommand() throws CommandException, Exception {
		ADL in = getInitialADL();

		Delete delete = new Delete();
		delete.fragmentId = "two";
		delete.fragmentHash = "5e9a0d9f2773b0209b111884bae251e26e1c1d88";
		delete.cascade = true;
		
		ADL out = commandController.applyCommand(Collections.singletonList(delete), in);
		performSaveAndCheck(out, "delete");
	}


	public void performSaveAndCheck(ADL out, String name) throws Exception {
		String result = out.getAsXMLString();
		TestingHelp.writeOutput(this.getClass(), null, name+".xml", result);
		String expected4 = StreamUtils.copyToString(this.getClass().getResourceAsStream("/commands/after_"+name+".xml"), Charset.forName("UTF-8"));
		XMLCompare.compareXML(expected4, result);
	}
	
	@Test
	public void testSetTextCommand() throws CommandException, Exception {
		ADL in = getInitialADL();
		
		SetText setText = new SetText();
		setText.newText =  "Winner";
		setText.fragmentId="two";
		setText.fragmentHash = "5e9a0d9f2773b0209b111884bae251e26e1c1d88";
		
		ADL out = commandController.applyCommand(Collections.singletonList(setText), in);
		performSaveAndCheck(out, "settext");
	}
	
	@Test
	public void testSetAttrCommand() throws CommandException, Exception {
		ADL in = getInitialADL();
		
		SetAttr setAttr = new SetAttr();
		setAttr.name =  "name";
		setAttr.value = "value";
		setAttr.fragmentId="two";
		setAttr.fragmentHash = "5e9a0d9f2773b0209b111884bae251e26e1c1d88";
		
		ADL out = commandController.applyCommand(Collections.singletonList(setAttr), in);
		performSaveAndCheck(out, "setattr");
	}
	
	@Test
	public void testCopyCommand() throws CommandException, Exception {
		ADL in = getInitialADL();
		
		String uri = this.getClass().getResource("/commands/test_command1.xml").toString()+"#one";
		
		Copy copy = new Copy();
		copy.uriStr =  uri;
		copy.fragmentId="The Diagram";
		copy.beforeFragmentId="two";
		copy.fragmentHash = "344d34b5c8803e8191dbaef1f0ebd698bde223b3";
		
		ADL out = commandController.applyCommand(Collections.singletonList(copy), in);
		performSaveAndCheck(out, "copy");
	}
}
