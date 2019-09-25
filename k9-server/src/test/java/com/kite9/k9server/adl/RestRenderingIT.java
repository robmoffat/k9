package com.kite9.k9server.adl;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StreamUtils;

import com.kite9.k9server.XMLCompare;
import com.kite9.k9server.adl.format.media.MediaTypes;
import com.kite9.k9server.domain.AbstractLifecycleTest;
import com.kite9.k9server.resource.DocumentResource;
import com.kite9.k9server.resource.ProjectResource;


public class RestRenderingIT extends AbstractLifecycleTest {
	
	String docUrl;
	String projectUrl;

	protected byte[] load(String page, MediaType mt) throws Exception {
		HttpHeaders headers = createJWTTokenHeaders(jwtToken, MediaType.APPLICATION_JSON, mt);
		HttpEntity<Void> ent = new HttpEntity<>(headers);
		ResponseEntity<byte[]> back = getRestTemplate().exchange(new URI(page), HttpMethod.GET, ent, byte[].class);
		return back.getBody();
	}
	
	@Before
	public void testVariousMarkups() throws URISyntaxException {
		ProjectResource pr = createAProjectResource();
		this.projectUrl = pr.getLink("self").getHref();
		DocumentResource dr  = createADocumentResource(pr);
		this.docUrl = dr.getLink("self").getHref();
	}

	@After
	public void destroyDocument() throws URISyntaxException {
		delete(new URI(docUrl));
		delete(new URI(projectUrl));
	}

	
//	
//	@Test
//	public void testRestPNG() throws Exception {
//		byte[] png = loadStaticPNG("/api/documents/3");
//		persistInAFile(png, "testRestPNG", "diagram.png");
//		byte[] expected = StreamUtils.copyToByteArray(this.getClass().getResourceAsStream("/rendering/public/testRestPNG/diagram.png"));
//		Assert.assertEquals(expected.length, png.length);
//	}
	
	@Test
	public void testRestHTML() throws Exception {
		testMarkupFormat(MediaType.TEXT_HTML, "testRest", "diagram.html");
	}
	
	@Test
	public void testRestADLPlusSVG() throws Exception {
		testMarkupFormat(MediaTypes.ADL_SVG, "testRest", "diagram.xml");
	}
	
//	@Test
//	public void testRestSVG() throws Exception {
//		byte[] svg = loadStaticSVG(docUrl);
//		persistInAFile(svg, "testExampleSVG", "diagram.svg");
//		String expected = StreamUtils.copyToString(this.getClass().getResourceAsStream("/rendering/public/testExampleSVG/diagram.svg"), Charset.forName("UTF-8"));
//		XMLCompare.compareXML(new String(svg), expected);
//	}
	

	protected void testMarkupFormat(MediaType format, String path, String file) throws Exception {
		byte[] bytes = load(docUrl, format);
		String actual = new String(bytes);
		
		// remove unwanted stuff
		actual = actual.replaceAll("localhost:"+port, "localhost:xxxx");
		actual = actual.replaceAll("<dateCreated>.*</dateCreated>", "<dateCreated>xxxx</dateCreated>");
		persistInAFile(actual.getBytes(), path, file);
		
		String expected = StreamUtils.copyToString(this.getClass().getResourceAsStream("/rendering/public/"+path+"/"+file), Charset.forName("UTF-8"));
		Assert.assertTrue(actual.contains(expected));
	}
}
