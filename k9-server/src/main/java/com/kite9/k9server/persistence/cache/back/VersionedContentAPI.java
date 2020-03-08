package com.kite9.k9server.persistence.cache.back;

import java.io.InputStream;
import java.util.List;

import com.kite9.k9server.command.content.HasOperations;

/**
 * This is a concise, github-centric API built around version ids.
 */
public interface VersionedContentAPI<K> extends HasOperations {

	/**
	 * Returns the revision id, if one has been set.
	 */
	public K getCurrentRevisionID();
	
	/**
	 * Commits a new revision.  Returns a revision id if one is set.
	 */
	public K commitRevision(byte[] contents, String message);
	
	public K commitRevision(String contents, String message);
	
	/**
	 * Returns key for each of the known versions
	 */
	public List<K> getVersionHistory();
	
	/**
	 * Returns the content of a particular version
	 */
	public InputStream getVersionContent(K k);

	/**
	 * Potentially quicker than undo/redo - allows you to set
	 * a specific version
	 */
	public void updateCurrentRevision(K k);

}
