package com.kite9.k9server.domain;

import org.junit.Test;

import com.kite9.k9server.resource.DocumentResource;
import com.kite9.k9server.resource.ProjectResource;

public class DocumentCommandPostingTest extends AbstractLifecycleTest {

	@Test
	public void testCommandPosting() throws Exception {
		ProjectResource pr = createAProjectResource();
		DocumentResource dr  = createADocumentResource(pr);
		
	}
}
