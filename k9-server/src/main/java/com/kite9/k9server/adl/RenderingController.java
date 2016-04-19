package com.kite9.k9server.adl;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Handles rendering of Kite9 ADL content.
 * 
 * @author robmoffat
 *
 */
@Controller
public class RenderingController {

	@RequestMapping(path="/api/renderer")
	public @ResponseBody ADL echo(@RequestBody ADL input, @RequestHeader HttpHeaders headers) {
		return input;
	}
}
