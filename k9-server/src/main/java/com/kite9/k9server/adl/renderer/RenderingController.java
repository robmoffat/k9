package com.kite9.k9server.adl.renderer;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.kite9.k9server.adl.format.media.MediaTypes;
import com.kite9.k9server.adl.holder.ADL;
import com.kite9.k9server.adl.holder.ADLImpl;

/**
 * Handles rendering of Kite9 ADL content.  A bit like a simpler version of the CommandController, 
 * except that it just renders the ADL.
 * 
 * @author robmoffat
 *
 */
@Controller()
public class RenderingController {
	
	@RequestMapping(path="/api/renderer", consumes= {MediaTypes.ADL_SVG_VALUE, MediaTypes.SVG_VALUE})
	public @ResponseBody ADL echo(@RequestBody ADL input) {
		return input;
	}
	
	@RequestMapping(path="/api/renderer/test")
	public @ResponseBody ADL testCard() throws IOException {
		String xml = StreamUtils.copyToString(this.getClass().getResourceAsStream("/test-card.xml"), Charset.defaultCharset());
		return new ADLImpl(xml, "someurl");
	}
	
//	@RequestMapping(path="/api/renderer/static/{partA}/{partB:.+}")
//	public @ResponseBody ADL loadStatic2(
//			@PathVariable("partA") String partA, 
//			@PathVariable("partB") String partB, 
//			HttpServletRequest request) throws IOException {
//		String resourceName = "/static/internal/"+partA+"/"+partB;
//		String xml = loadXML(resourceName);
//		String url = request.getRequestURL().toString();
//		return new ADLImpl(xml, url);
//	}
	
	@RequestMapping(path="/public/{partA}.html")
	public @ResponseBody ADL loadStaticHtml(@PathVariable("partA") String partA, HttpServletRequest request) throws IOException {
		String resourceName = "/static/public/"+partA;
		String url = request.getRequestURL().toString();
		String xml = loadXML(resourceName);
		return new ADLImpl(xml, url);
	}
	
	@RequestMapping(path="/public/{partA}.svg")
	public @ResponseBody ADL loadStaticSvg(@PathVariable("partA") String partA, HttpServletRequest request) throws IOException {
		String resourceName = "/static/public/"+partA;
		String url = request.getRequestURL().toString();
		String xml = loadXML(resourceName);
		return new ADLImpl(xml, url);
	}
	
	private String loadXML(String resourceName) throws IOException {
		InputStream resourceAsStream = this.getClass().getResourceAsStream(resourceName);
		if (resourceAsStream == null) {
			resourceAsStream = this.getClass().getResourceAsStream(resourceName+ ".xml");
		}
		return StreamUtils.copyToString(resourceAsStream, Charset.defaultCharset());
	}
	
}
