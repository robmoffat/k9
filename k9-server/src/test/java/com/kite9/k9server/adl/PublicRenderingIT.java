package com.kite9.k9server.adl;

import java.net.URI;
import java.nio.charset.Charset;
import java.util.Arrays;

import org.junit.Assert;
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

	protected byte[] loadStaticHtml(String page) throws Exception {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Arrays.asList(MediaType.TEXT_HTML));
		HttpEntity<Void> ent = new HttpEntity<>(headers);
		ResponseEntity<byte[]> back = getRestTemplate().exchange(new URI(getUrlBase()+page), HttpMethod.GET, ent, byte[].class);
		return back.getBody();
	}
	
	protected byte[] loadStaticPNG(String page) throws Exception {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Arrays.asList(MediaType.IMAGE_PNG));
		HttpEntity<Void> ent = new HttpEntity<>(headers);
		ResponseEntity<byte[]> back = getRestTemplate().exchange(new URI(getUrlBase()+page), HttpMethod.GET, ent, byte[].class);
		return back.getBody();
	}
	
	protected byte[] loadStaticSVG(String page) throws Exception {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Arrays.asList(MediaTypes.SVG));
		HttpEntity<Void> ent = new HttpEntity<>(headers);
		ResponseEntity<byte[]> back = getRestTemplate().exchange(new URI(getUrlBase()+page), HttpMethod.GET, ent, byte[].class);
		return back.getBody();
	}
	
	@Test
	public void testExampleHTML() throws Exception {
		byte[] html = loadStaticHtml("/public/examples/risk-first/example.html");
		persistInAFile(html, "testExampleHTML", "diagram.html");
		String expected = StreamUtils.copyToString(this.getClass().getResourceAsStream("/rendering/public/testExampleHTML/diagram.html"), Charset.forName("UTF-8"));
		XMLCompare.compareXML(expected, new String(html));
	}
	
	@Test
	public void testExampleSVG() throws Exception {
		byte[] svg = loadStaticSVG("/public/examples/risk-first/example.svg");
		persistInAFile(svg, "testExampleSVG", "diagram.svg");
		String expected = StreamUtils.copyToString(this.getClass().getResourceAsStream("/rendering/public/testExampleSVG/diagram.svg"), Charset.forName("UTF-8"));
		XMLCompare.compareXML(new String(svg), expected);
	}
	
	@Test
	public void testExamplePNG() throws Exception {
		byte[] png = loadStaticPNG("/public/examples/risk-first/example.png");
		persistInAFile(png, "testExamplePNG", "diagram.png");
		byte[] expected = StreamUtils.copyToByteArray(this.getClass().getResourceAsStream("/rendering/public/testExamplePNG/diagram.png"));
		Assert.assertEquals(expected.length, png.length);
	}
}
