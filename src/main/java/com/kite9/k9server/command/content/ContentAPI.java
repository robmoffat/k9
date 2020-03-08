package com.kite9.k9server.command.content;

import java.io.InputStream;

/**
 * This is an API that controls updating/reading a single diagram with some kind of backing storage.
 * 
 * @author robmoffat
 *
 */
public interface ContentAPI extends HasOperations, UndoableAPI<InputStream> {
	
	/**
	 * Commits a new revision.  Returns a revision id if one is set.
	 */
	public void commitRevision(byte[] contents, String message);
	
	public void commitRevision(String contents, String message);

}
