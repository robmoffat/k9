package com.kite9.k9server.persistence.cache.front;

import java.io.InputStream;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * A cache element with which you can create input streams. 
 * Can also know it's revision id, if there is one.
 */
public abstract class InputStreamCache<K> implements Supplier<InputStream>, Consumer<K> {
	
	private K revisionID;
	
	public InputStreamCache(K revisionID) {
		super();
		this.revisionID = revisionID;
	}

	public InputStreamCache() {
		super();
	}

	public K getRevisionID() {
		return revisionID;
	}

	@Override
	public void accept(K t) {
		this.revisionID = t;
	}

	
}