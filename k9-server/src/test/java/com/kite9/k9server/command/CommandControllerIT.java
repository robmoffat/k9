package com.kite9.k9server.command;

import java.net.URI;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kite9.k9server.adl.format.media.Kite9MediaTypes;
import com.kite9.k9server.domain.AbstractLifecycleTest;


@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment=WebEnvironment.RANDOM_PORT)
//@AutoConfigureMockMvc	
public class CommandControllerIT extends AbstractLifecycleTest {

//	@Autowired
//	private MockMvc mockMvc;

	ObjectMapper om = new ObjectMapper();
	
//	@MockBean
//	MailSender ms;
	
	@LocalServerPort
	int port;
	
	@Test
	public void testNullCommand() throws Exception {
			
		String docUrl = "http://localhost:"+port+"/public/commands/test_command1.xml";
		String commandUrl = "http://localhost:"+port+"/api/command/v1?on="+docUrl;
		
		byte[] out = postCommand("[]", new URI(commandUrl));
		String res = new String(out);
		Assert.assertTrue(res.contains("<label id=\"two-label\">Two</label>"));		
	}
	
	@Test
	public void testDeleteCommand() throws Exception {
			
		String docUrl = "http://localhost:"+port+"/public/commands/test_command1.xml";
				
		String step = "{\"type\": \"Delete\", \"fragmentId\": \"two-label\", \"fragmentHash\": \"0d168968280ce460f11629f27fbd21156c7bc6cf\"}";
		
		String commandUrl = "http://localhost:"+port+"/api/command/v1?on="+docUrl;
		
		byte[] out = postCommand("["+step+"]", new URI(commandUrl));
		String res = new String(out);
		Assert.assertTrue(res.contains("    <glyph id=\"two\">\n" + 
	    				"      \n" + 
	    				"    </glyph>"));
	}
	

	private byte[] postCommand(String commands, URI uri) {
		RequestEntity<byte[]> in = new RequestEntity<>(commands.getBytes(), createNoAuthHeaders(MediaType.APPLICATION_JSON, Kite9MediaTypes.ADL_SVG), HttpMethod.POST, uri);
		ResponseEntity<byte[]> dOut = restTemplate.exchange(in, byte[].class);
		return dOut.getBody();
	}	
}

