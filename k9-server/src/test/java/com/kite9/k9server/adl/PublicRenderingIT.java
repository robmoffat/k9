package com.kite9.k9server.adl;

import java.net.URI;
import java.nio.charset.Charset;
import java.util.Arrays;

import org.junit.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StreamUtils;

import com.kite9.k9server.AbstractRestIT;
import com.kite9.k9server.XMLCompare;
import com.kite9.k9server.adl.format.media.MediaTypes;

public class PublicRenderingIT extends AbstractRestIT {

	protected HttpHeaders createKite9AuthHeaders(MediaType in, MediaType... accept) {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Arrays.asList(accept));
		if (in != null) {
			headers.setContentType(in);
		}
		return headers;
	}
	
	protected byte[] loadStaticHtml(String page) throws Exception {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Arrays.asList(MediaType.TEXT_HTML));
		HttpEntity<Void> ent = new HttpEntity<>(headers);
		ResponseEntity<byte[]> back = getRestTemplate().exchange(new URI(urlBase+page), HttpMethod.GET, ent, byte[].class);
		return back.getBody();
	}
	
	protected byte[] loadStaticSVG(String page) throws Exception {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Arrays.asList(MediaTypes.SVG));
		HttpEntity<Void> ent = new HttpEntity<>(headers);
		ResponseEntity<byte[]> back = getRestTemplate().exchange(new URI(urlBase+page), HttpMethod.GET, ent, byte[].class);
		return back.getBody();
	}
	
	@Test
	public void testExampleHtml() throws Exception {
		byte[] html = loadStaticHtml("/public/examples/risk-first/dependency-risk-fit.html");
		String expected = StreamUtils.copyToString(this.getClass().getResourceAsStream("/dependency_risk_fit_output.html"), Charset.forName("UTF-8"));
		XMLCompare.compareXML(new String(html), expected);
	}
	
	@Test
	public void testExampleSVG() throws Exception {
		byte[] html = loadStaticSVG("/public/examples/risk-first/dependency-risk-fit.svg");
		String expected = StreamUtils.copyToString(this.getClass().getResourceAsStream("/dependency_risk_fit_output.svg"), Charset.forName("UTF-8"));
		XMLCompare.compareXML(new String(html), expected);
	}
}
