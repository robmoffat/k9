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
	
	public enum Operation { REDO, UNDO };

	public InputStream getCurrentRevisionContent();
	
	public void commitRevision(byte[] contents, String message);
	
	public void commitRevision(String contents, String message);
	
	public InputStream undo();
	
	public InputStream redo();
	
	public ContentAPI withPath(String ext);
	
	public EnumSet<Operation> getOperations();
	
}
