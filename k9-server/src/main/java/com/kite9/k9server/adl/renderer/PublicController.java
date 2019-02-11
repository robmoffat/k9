package com.kite9.k9server.adl.renderer;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

import javax.servlet.http.HttpServletRequest;

import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.kite9.k9server.adl.format.media.MediaTypes;
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
	public @ResponseBody ADL loadStaticHtml(HttpServletRequest request) throws IOException {
		String url = request.getRequestURL().toString();
		String stub = url.substring(url.indexOf("/public/")+8, url.lastIndexOf(".html"));
		String resourceName = "/static/public/"+stub;
		String xml = loadXML(resourceName);
		return new ADLImpl(xml, url);
	}
	
	@GetMapping(path="/public/**/*.svg", produces=MediaTypes.SVG_VALUE)
	public @ResponseBody ADL loadStaticSvg(HttpServletRequest request) throws IOException {
		String url = request.getRequestURL().toString();
		String stub = url.substring(url.indexOf("/public/")+8, url.lastIndexOf(".svg"));
		String resourceName = "/static/public/"+stub;
		String xml = loadXML(resourceName);
		return new ADLImpl(xml, url);
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
