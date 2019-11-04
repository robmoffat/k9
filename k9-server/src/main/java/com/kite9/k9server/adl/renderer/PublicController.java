package com.kite9.k9server.adl.renderer;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.kite9.k9server.adl.format.media.Kite9MediaTypes;
import com.kite9.k9server.adl.holder.ADL;
import com.kite9.k9server.adl.holder.ADLImpl;

/**
 * Returns content held within the public folder of the project, which anyone can access.
 * 
 * @author robmoffat
 *
 */
@Controller()
public class PublicController {
	
	@GetMapping(path="/public/**/*.html", produces=MediaType.TEXT_HTML_VALUE)
	public ResponseEntity<?> loadStaticHtml(RequestEntity<?> request) throws Exception {
		String url = request.getUrl().toString();
		String stub = url.substring(url.indexOf("/public/")+8, url.lastIndexOf(".html"));
		String resourceName = "/static/public/"+stub;
		String xml = loadXML(resourceName);
		return new ResponseEntity<ADL>(ADLImpl.xmlMode(request.getUrl(), xml, request.getHeaders()), HttpStatus.OK);
	}
	
	@GetMapping(path="/public/**/*.png", produces=MediaType.IMAGE_PNG_VALUE)
	public @ResponseBody ADL loadStaticPng(RequestEntity<?> request) throws Exception {
		String url = request.getUrl().toString();
		String stub = url.substring(url.indexOf("/public/")+8, url.lastIndexOf(".png"));
		String resourceName = "/static/public/"+stub;
		String xml = loadXML(resourceName);
		return ADLImpl.xmlMode(request.getUrl(), xml, request.getHeaders());
	}
	
	@GetMapping(path="/public/**/*.svg", produces=Kite9MediaTypes.SVG_VALUE)
	public @ResponseBody ADL loadStaticSvg(RequestEntity<?> request) throws Exception {
		String url = request.getUrl().toString();
		String stub = url.substring(url.indexOf("/public/")+8, url.lastIndexOf(".svg"));
		String resourceName = "/static/public/"+stub;
		String xml = loadXML(resourceName);
		return ADLImpl.xmlMode(request.getUrl(), xml, request.getHeaders());
	}
	
	private String loadXML(String resourceName) throws IOException {
		InputStream resourceAsStream = this.getClass().getResourceAsStream(resourceName);
		
		if (resourceAsStream == null) {
			resourceAsStream = this.getClass().getResourceAsStream(resourceName+ ".xml");
		}
		
		if (resourceAsStream == null) {
			resourceAsStream = this.getClass().getResourceAsStream(resourceName+ ".svg");
		} 
		
		if (resourceAsStream == null) {
			resourceAsStream = this.getClass().getResourceAsStream(resourceName+ "/index.xml");
		}
		
		if (resourceAsStream == null) {
			throw new ResourceNotFoundException(resourceName);
		}
		return StreamUtils.copyToString(resourceAsStream, Charset.defaultCharset());
	}
	
}
