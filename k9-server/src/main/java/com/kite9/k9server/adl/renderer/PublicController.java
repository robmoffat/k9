package com.kite9.k9server.adl.renderer;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.concurrent.TimeUnit;

import org.springframework.core.io.InputStreamResource;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.GetMapping;

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
		return loadStatic(request, "html");
	}
	
	@GetMapping(path="/public/**/*.png", produces=MediaType.IMAGE_PNG_VALUE)
	public ResponseEntity<?> loadStaticPng(RequestEntity<?> request) throws Exception {
		return loadStatic(request, "png");
	}
	
	@GetMapping(path="/public/**/*.svg", produces=Kite9MediaTypes.SVG_VALUE)
	public ResponseEntity<?> loadStaticSvg(RequestEntity<?> request) throws Exception {
		return loadStatic(request, "svg");
	}
	
	@GetMapping(path="/public/**/*.xml", produces= { Kite9MediaTypes.ADL_SVG_VALUE, MediaType.APPLICATION_XML_VALUE, MediaType.TEXT_XML_VALUE})
	public ResponseEntity<?> loadStaticXml(RequestEntity<?> request) throws Exception {
		return loadStatic(request, "xml");
	}
	
	protected ResponseEntity<?> loadStatic(RequestEntity<?> request, String type) throws Exception {
		String url = request.getUrl().toString();
		String stub = url.substring(url.indexOf("/public/")+8, url.lastIndexOf("."));
		String resourceStub = "/static/public/"+stub;
		
		if (type.equals("html")) {
			InputStreamResource htmlStreamResource = checkForResource(resourceStub + ".html");
			if (htmlStreamResource != null) {
				return new ResponseEntity<InputStreamResource>(htmlStreamResource, HttpStatus.OK);
			}
		}
		
		if (type.equals("png")) {
			InputStreamResource pngStreamResource = checkForResource(resourceStub + ".png");
			if (pngStreamResource != null) {
				return new ResponseEntity<InputStreamResource>(pngStreamResource, HttpStatus.OK);
			}
		}
		
		if (!type.equals("xml")) { // SVG
			// check to see if we have a pre-existing svg file
			String xml = loadXML(resourceStub + ".svg");
			if (xml != null) {
				return new ResponseEntity<ADL>(ADLImpl.xmlMode(request.getUrl(), xml, request.getHeaders()), createCachingHeaders(), HttpStatus.OK);
			}
		}
		
		// ok, we need to transform one
		String xml = loadXML(resourceStub + ".xml");
		if (xml != null) {
			return new ResponseEntity<ADL>(ADLImpl.xmlMode(request.getUrl(), xml, request.getHeaders()), HttpStatus.OK);
		}
		
		return new ResponseEntity<ADL>(HttpStatus.NOT_FOUND);
	}
	
	/*
	 * If we have svg files on the server, they're probably not going to change
	 * much and should be cached.
	 */
	private MultiValueMap<String, String> createCachingHeaders() {
		CacheControl cc = CacheControl.maxAge(5, TimeUnit.DAYS);
		HttpHeaders h = new HttpHeaders();
		h.add(HttpHeaders.CACHE_CONTROL, cc.getHeaderValue());
		return h;
	}

	private InputStreamResource checkForResource(String resourceName) {
		InputStream is = this.getClass().getResourceAsStream(resourceName);
		if (is != null) {
			InputStreamResource isr = new InputStreamResource(is);
			return isr;
		} else {
			return null;
		}
	}
	
	private String loadXML(String resourceName) throws IOException {
		InputStream resourceAsStream = this.getClass().getResourceAsStream(resourceName);
		
		if (resourceAsStream == null) {
			return null;
		}
		
		return StreamUtils.copyToString(resourceAsStream, Charset.defaultCharset());
	}
	
}
