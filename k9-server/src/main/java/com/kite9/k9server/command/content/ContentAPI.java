package com.kite9.k9server.command.content;

import java.io.InputStream;
import java.util.EnumSet;

/**
 * This is an API that controls updating/reading a single diagram with some kind of backing storage.
 * 
 * @author robmoffat
 *
 */
public interface ContentAPI {
	
	public enum Operation { REDO, UNDO, COMMIT };

	public InputStream getCurrentRevisionContent();
	
	public void commitRevision(byte[] contents, String message);
	
	public void commitRevision(String contents, String message);
	
	/**
	 * Returns input stream for previous version, or current version again if
	 * undo can't be done
	 */
	public InputStream undo();
	
	/**
	 * Returns input stream for previous version, or current version again if
	 * redo can't be done
	 */
	public InputStream redo();
	
	public ContentAPI withPath(String ext);
	
	public EnumSet<Operation> getOperations();
	
}
