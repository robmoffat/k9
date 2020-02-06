package com.kite9.k9server.security;

import java.net.URI;

import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import com.kite9.k9server.adl.format.media.Kite9MediaTypes;
import com.kite9.k9server.adl.renderer.PublicController;

@Controller
public class LoginController {

	@GetMapping(path="/login", produces= {MediaType.TEXT_HTML_VALUE})
	public ResponseEntity<?> loginUrl(RequestEntity<?> request) throws Exception {
		URI orig = request.getUrl();
		URI newUri = new URI(orig.getScheme(), 
				orig.getUserInfo(), 
				orig.getHost(), 
				orig.getPort(), 
				"/public/examples/admin/index.html", 
				null,
				null);
		return PublicController.loadStatic(newUri, request.getHeaders(), "html");
	}
	

	@GetMapping(path="/login-failed", produces= {MediaType.TEXT_HTML_VALUE, Kite9MediaTypes.SVG_VALUE})
	public ResponseEntity<?> loginFailedUrl(RequestEntity<?> request) throws Exception {
		URI orig = request.getUrl();
		URI newUri = new URI(orig.getScheme(), 
				orig.getUserInfo(), 
				orig.getHost(), 
				orig.getPort(), 
				"/public/examples/admin/failed.html", 
				null,
				null);
		return PublicController.loadStatic(newUri, request.getHeaders(), "html");
	}
}
