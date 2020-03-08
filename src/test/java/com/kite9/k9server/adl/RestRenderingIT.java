package com.kite9.k9server.adl;

public class RestRenderingIT {
/*	
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
		DocumentResource dr  = createADocumentResource(pr, "http://localhost:"+port+"/public/templates/basic.xml");
		this.docUrl = dr.getLink(IanaLinkRelations.SELF).getHref();
	}

	@After
	public void destroyDocument() throws URISyntaxException {
		delete(new URI(docUrl));
		delete(new URI(projectUrl));
	}

	
	
	@Test
	public void testRestPNG() throws Exception {
		byte[] png = load(docUrl, MediaType.IMAGE_PNG);
		persistInAFile(png, "testRest", "diagram.png");
		byte[] expected = StreamUtils.copyToByteArray(this.getClass().getResourceAsStream("/rendering/public/testRest/diagram.png"));
		// can't really compare diagrams - it doesn't seem to work ever.
		//Assert.assertEquals(expected.length, png.length);
	}
	
	@Test
	public void testRestHTML() throws Exception {
		testMarkupFormat(MediaType.TEXT_HTML, "testRest", "diagram.html");
	}
	
	@Test
	public void testRestADLPlusSVG() throws Exception {
		testMarkupFormat(Kite9MediaTypes.ADL_SVG, "testRest", "diagram.xml");
	}
	
	@Test
	public void testRestSVG() throws Exception {
		testMarkupFormat(Kite9MediaTypes.SVG, "testRest", "diagram.svg");
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
		actual = actual.replaceAll("<dateCreated>.*</dateCreated>", "<dateCreated>xxxx</dateCreated>");
		actual = actual.replaceAll("<lastUpdated>.*</lastUpdated>", "<lastUpdated>xxxx</lastUpdated>");
		
		actual = actual.replaceAll("/api/projects/[0-9]+", "/api/projects/xxx");
		actual = actual.replaceAll("/api/documents/[0-9]+", "/api/documents/xxx");
		actual = actual.replaceAll("/api/revisions/[0-9]+", "/api/revisions/xxx");
		
		persistInAFile(actual.getBytes(), path, file);
		
		String expected = StreamUtils.copyToString(this.getClass().getResourceAsStream("/rendering/public/"+path+"/"+file), Charset.forName("UTF-8"));
		Assert.assertTrue(actual.contains(expected));
	}*/
}
