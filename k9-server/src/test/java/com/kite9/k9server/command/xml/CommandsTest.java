package com.kite9.k9server.command.xml;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kite9.framework.common.TestingHelp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.mail.MailSender;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.StreamUtils;

import com.kite9.k9server.XMLCompare;
import com.kite9.k9server.adl.holder.ADL;
import com.kite9.k9server.adl.holder.ADLImpl;
import com.kite9.k9server.command.Command;
import com.kite9.k9server.command.CommandController;
import com.kite9.k9server.command.CommandException;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment=WebEnvironment.RANDOM_PORT)
public class CommandsTest {
	
	public static final String END_SVG_DOCUMENT = "</svg:svg>";

	public static final String NS = " xmlns=\"http://www.kite9.org/schema/adl\"\n  xmlns:svg='http://www.w3.org/2000/svg' ";

	public static final String START_SVG_DOCUMENT = "<svg:svg xmlns:xlink='http://www.w3.org/1999/xlink' " + NS + ">";
		
	@MockBean
	MailSender mailSender;
	
	@Autowired
	CommandController commandController;
	
	@LocalServerPort
	protected int port;
	
	String sourceURI;
	
	@Before
	public void setupUrl() {
		sourceURI = "http://localhost:"+port+"/public/commands/test_command1.xml";
	}
	

	@Test
	public void testReplaceCommand1() throws CommandException, Exception {
		Replace replace = new Replace();
		replace.fragmentId = "link";
		replace.uriStr= "#one";
		replace.approach = Replace.Approach.SHALLOW;
		replace.keptAttributes = Arrays.asList("rank", "id");
	
		
		ADL out = commandController.applyCommandOnStatic(buildRequestEntity(replace), sourceURI);
		performSaveAndCheck(out, "replace");

	}
	
	@Test
	public void testReplaceCommand2() throws CommandException, Exception {
		Replace replace = new Replace();
		replace.fragmentId = "link";
		replace.uriStr= "#one";
		replace.approach = Replace.Approach.DEEP;
		replace.keptAttributes = Arrays.asList("rank", "id");
		
		ADL out = commandController.applyCommandOnStatic(buildRequestEntity(replace), sourceURI);
		performSaveAndCheck(out, "replace-all");

	}
	
	@Test
	public void testReplaceCommand3() throws CommandException, Exception {
		Replace replace = new Replace();
		replace.fragmentId = "one";
		replace.uriStr= "#link-to";
		replace.approach = Replace.Approach.ATTRIBUTES;
		replace.keptAttributes = Arrays.asList("rank", "id");
		
		ADL out = commandController.applyCommandOnStatic(buildRequestEntity(replace), sourceURI);
		performSaveAndCheck(out, "replace-attr");

	}
	
	@Test
	public void testMoveCommand() throws CommandException, Exception {
		Move move = new Move();
		move.moveId = "one-label";
		move.fragmentId = "two";
		move.beforeFragmentId = "two-label";
		
		ADL out = commandController.applyCommandOnStatic(buildRequestEntity(move), sourceURI);
		performSaveAndCheck(out, "move");
	}
	
	@Test
	public void testDeleteCommand() throws CommandException, Exception {
		Delete delete = new Delete();
		delete.fragmentId = "two";
		delete.cascade = true;
		
		ADL out = commandController.applyCommandOnStatic(buildRequestEntity(delete), sourceURI);
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
		SetText setText = new SetText();
		setText.newText =  "Winner";
		setText.fragmentId="two";
		
		ADL out = commandController.applyCommandOnStatic(buildRequestEntity(setText), sourceURI);
		performSaveAndCheck(out, "settext");
	}
	
	@Test
	public void testSetAttrCommand() throws CommandException, Exception {
		SetAttr setAttr = new SetAttr();
		setAttr.name =  "name";
		setAttr.value = "value";
		setAttr.fragmentId="two";
		
		ADL out = commandController.applyCommandOnStatic(buildRequestEntity(setAttr), sourceURI);
		performSaveAndCheck(out, "setattr");
	}
	
	private RequestEntity<List<Command>> buildRequestEntity(Command c) throws URISyntaxException {
		RequestEntity<List<Command>> out = new RequestEntity<>(Collections.singletonList(c), HttpMethod.POST, new URI(""));
		return out;
	}


	@Test
	public void testCopyCommand() throws CommandException, Exception {
		String uri = sourceURI+"#one";
		
		Copy copy = new Copy();
		copy.uriStr =  uri;
		copy.fragmentId="The Diagram";
		copy.beforeFragmentId="two";
		copy.deep = true;
		
		ADL out = commandController.applyCommandOnStatic(buildRequestEntity(copy), sourceURI);
		performSaveAndCheck(out, "copy");
	}
}
