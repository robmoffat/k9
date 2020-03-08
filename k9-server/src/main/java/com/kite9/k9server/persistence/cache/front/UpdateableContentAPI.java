package com.kite9.k9server.persistence.cache.front;

import java.util.function.Consumer;

import com.kite9.k9server.command.content.HasOperations;
import com.kite9.k9server.command.content.UndoableAPI;
import com.kite9.k9server.persistence.cache.Caching;


public interface UpdateableContentAPI<K> extends HasOperations, UndoableAPI<InputStreamCache<K>>, Caching {

	/**
	 * Builds a new revision, and creates a consumer so the backing
	 * cache can set the revision ID when it exists.
	 */
	public Consumer<K> commitRevision(byte[] contents, String message);
	
	/**
	 * Builds a new revision, and creates a consumer so the backing
	 * cache can set the revision ID when it exists.
	 */
	public Consumer<K> commitRevision(String contents, String message);
	
	
}
