package com.kite9.k9server.adl.renderer;

import java.net.URI;
import java.nio.charset.Charset;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.kite9.k9server.adl.format.media.Kite9MediaTypes;
import com.kite9.k9server.adl.holder.ADL;
import com.kite9.k9server.adl.holder.ADLImpl;

/**
 * Handles rendering of Kite9 ADL content.  A bit like a simpler version of the CommandController, 
 * except that it just renders the ADL.
 * 
 * @author robmoffat
 *
 */
@Controller
public class PostController {
	
	@RequestMapping(path="/api/renderer", consumes= {Kite9MediaTypes.ADL_SVG_VALUE, Kite9MediaTypes.SVG_VALUE})
	public @ResponseBody ADL echo(@RequestBody ADL input, HttpServletRequest request) throws Exception {
		input.setUri(new URI(request.getRequestURL().toString()));
		return input;
	}
	
	@RequestMapping(path="/api/renderer/test")
	public @ResponseBody ADL testCard(RequestEntity<ADL> request) throws Exception {
		String xml = StreamUtils.copyToString(this.getClass().getResourceAsStream("/test-card.xml"), Charset.defaultCharset());
		return ADLImpl.xmlMode(request.getUrl(), xml, request.getHeaders());
	}
}
