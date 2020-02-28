package com.kite9.k9server.command.content;

import java.io.InputStream;

/**
 * This is an API that controls updating/reading a single diagram with some kind of backing storage.
 * 
 * @author robmoffat
 *
 */
public interface ContentAPI {

	/**
	 * Returns a specific revision, or the latest one, if rev is null or an empty string
	 */
	public InputStream getRevision(String rev);
	
	public String commitRevision(byte[] contents, String message);
	
	public String commitRevision(String contents, String message);
	
	public InputStream updateCurrentRevision(String revision);
}
