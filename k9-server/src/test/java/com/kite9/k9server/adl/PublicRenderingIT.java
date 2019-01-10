package com.kite9.k9server.adl;

import java.net.URI;
import java.util.Arrays;

import org.junit.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import com.kite9.k9server.AbstractRestIT;

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
		ResponseEntity<byte[]> back = getRestTemplate().exchange(new URI(urlBase+"/api/renderer/static/dependency-risk-fit"), HttpMethod.GET, ent, byte[].class);
		return back.getBody();
	}
	
	@Test
	public void testLandingPage() throws Exception {
		byte[] html = loadStaticHtml("/landing");
	}
}
