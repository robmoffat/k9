package com.kite9.k9server.persistence.cache;

import java.io.InputStream;
import java.util.List;

import com.kite9.k9server.command.content.ContentAPI;

public interface VersionedContentAPI<K> extends ContentAPI {

	/**
	 * Returns key for each of the known versions
	 */
	public List<K> getVersionHistory();
	
	/**
	 * Tells you which one of the versions in the history is the current one.
	 */
	public K getCurrentVersion();
	
	/**
	 * Returns the content of a particular version
	 */
	public InputStream getVersionContent(K k);

	@Override
	public VersionedContentAPI<K> withPath(String ext);
	
	
}
