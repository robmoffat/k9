package com.kite9.k9server.command.content;

import java.io.InputStream;
import java.util.List;

/**
 * This is an API that controls updating/reading a single diagram with some kind of backing storage.
 * 
 * @author robmoffat
 *
 */
public interface ContentAPI {

	public InputStream getCurrentRevisionContent();
	
	public String commitRevision(byte[] contents, String message);
	
	public String commitRevision(String contents, String message);
	
	public InputStream updateCurrentRevision(String revision);
	
	public ContentAPI withPath(String ext);
	
	public List<Version> getVersionHistory();
	
	public Version getCurrentVersion();

}
