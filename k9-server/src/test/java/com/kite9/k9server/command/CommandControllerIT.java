package com.kite9.k9server.command;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.hamcrest.core.StringContains;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.mail.MailSender;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kite9.k9server.adl.format.media.MediaTypes;


@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment=WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc	
public class CommandControllerIT {

	@Autowired
	private MockMvc mockMvc;

	ObjectMapper om = new ObjectMapper();
	
	@MockBean
	MailSender ms;
	
	@LocalServerPort
	int port;
	
	@Test
	public void testNullCommand() throws Exception {
			
		String docUrl = "http://localhost:"+port+"/public/commands/test_command1.xml";
				
		mockMvc.perform(
	        post("/api/command/v1?on="+docUrl)
	        	.content("[]")
	        	.contentType(MediaType.APPLICATION_JSON_VALUE)
	            .accept(MediaTypes.ADL_SVG_VALUE))
	    		.andDo(print())
	    		.andExpect(status().isOk())
	    		.andExpect(MockMvcResultMatchers.content().string(StringContains.containsString("<label id=\"two-label\">Two</label>")))
	    		.andReturn();
	}
	
	@Test
	public void testDeleteCommand() throws Exception {
			
		String docUrl = "http://localhost:"+port+"/public/commands/test_command1.xml";
				
		String step = "{\"type\": \"Delete\", \"fragmentId\": \"two-label\", \"fragmentHash\": \"0d168968280ce460f11629f27fbd21156c7bc6cf\"}";
		
		mockMvc.perform(
				 post("/api/command/v1?on="+docUrl)
				.content("["+step+"]")
	        	.contentType(MediaType.APPLICATION_JSON_VALUE)
	            .accept(MediaTypes.ADL_SVG_VALUE))
	    		.andDo(print())
	    		.andExpect(status().isOk())
	    		.andExpect(MockMvcResultMatchers.content().string(StringContains.containsString("    <glyph id=\"two\">\n" + 
	    				"      \n" + 
	    				"    </glyph>")))
	    		.andReturn();
	}
}

