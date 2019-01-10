package com.kite9.k9server.adl;

public class ExoticFormatsTest {

//	
//	protected String withBytesFromFile(MediaType output) throws IOException, URISyntaxException {
//		ByteArrayOutputStream baos = new ByteArrayOutputStream();
//		StreamHelp.streamCopy(this.getClass().getResourceAsStream("/test-card.xml"), baos, true);
//		HttpHeaders headers = createKite9AuthHeaders(u.api, MediaTypes.ADL_SVG, output);
//		RequestEntity<String> data = new RequestEntity<String>(new String(baos.toByteArray()), headers, HttpMethod.POST, new URI(urlBase+"/api/renderer"));
//		ResponseEntity<String> back= getRestTemplate().exchange(data, String.class);
//		return back.getBody();
//	}

//	@Test
//	@Ignore("No working PNG renderer right now")
//	public void testPNGRender() throws Exception {
//		byte[] back = withBytesInFormat(MediaType.IMAGE_PNG);
//		BufferedImage bi = ImageIO.read(new ByteArrayInputStream(back));
//		Assert.assertEquals(EXPECTED_WIDTH, bi.getWidth());
//		Assert.assertEquals(EXPECTED_HEIGHT, bi.getHeight());
//	}
	
//	@Test
//	@Ignore("No working PNG renderer right now")
//	public void testPNGRenderFromFile() throws Exception {
//		byte[] back = withBytesInFormat(MediaType.IMAGE_PNG);
//		BufferedImage bi = ImageIO.read(new ByteArrayInputStream(back));
//		File f = TestingHelp.prepareFileName(this.getClass(),"testPNGRenderFromFile", "diagram.png");
////		ImageIO.write(bi, "PNG", f);
//		persistInAFile(back, "testPNGRenderFromFile", "diagram.png");
//		
//		BufferedImage bi2 = ImageIO.read(new FileInputStream(f));
//		Assert.assertEquals(956, bi2.getWidth());
//	}

	
//	@Test
//	@Ignore("No working PDF renderer right now")
//	public void testPDFRender() throws Exception {
//		byte[] back = withBytesInFormat(MediaTypes.PDF);
//		
//		ByteArrayInputStream bais = new ByteArrayInputStream(back);		
//		PdfReader reader = new PdfReader(bais);
//		Assert.assertEquals(1, reader.getNumberOfPages());
//		Rectangle rect = reader.getPageSize(1);
//
//		Assert.assertEquals(EXPECTED_WIDTH, (int) rect.getWidth());
//		Assert.assertEquals(EXPECTED_HEIGHT, (int) rect.getHeight());
//	}	
}
