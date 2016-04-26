package com.kite9.k9server.adl;

import java.io.IOException;
import java.nio.charset.Charset;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.common.io.Resources;
import com.kite9.k9server.adl.format.MediaTypes;
import com.kite9.k9server.adl.holder.ADL;
import com.kite9.k9server.adl.holder.ADLImpl;

/**
 * Handles rendering of Kite9 ADL content.
 * 
 * @author robmoffat
 *
 */
@Controller
public class RenderingController {
	
	@RequestMapping(path="/api/renderer", 
		consumes= {MediaTypes.ADL_XML_VALUE, MediaTypes.RENDERED_ADL_XML_VALUE})
	public @ResponseBody ADL echo(@RequestBody ADL input) {
		return input;
	}
	
	@RequestMapping(path="/api/renderer/test")
	public @ResponseBody ADL testCard(@RequestHeader HttpHeaders headers) throws IOException {
		MediaType expected = headers.getAccept().get(0);
		String xml = Resources.toString(this.getClass().getResource("/test-card.xml"), Charset.defaultCharset());
		return new ADLImpl(xml, expected);
	}
	
}
