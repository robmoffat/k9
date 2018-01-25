package com.kite9.k9server.resource;

import org.apache.batik.util.ParsedURL;
import org.kite9.diagram.batik.format.ResourceReferencer;

/**
 * The ResourceRepository is the implementation of ResourceReferencer.  
 * This loads external (possibly relative) resources and places them in a common repository
 * so that we can use them all the time.
 */
public class ResourceRepository implements ResourceReferencer {

	@Override
	public Reference getReference(ParsedURL arg0) {
		System.out.println("TRYING TO LOAD URL: "+arg0);
	}

}
