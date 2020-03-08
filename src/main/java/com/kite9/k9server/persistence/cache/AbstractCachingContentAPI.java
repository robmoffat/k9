package com.kite9.k9server.persistence.cache;

public abstract class AbstractCachingContentAPI<K> implements CachingContentAPI {

	private long lastAccessTime;
	public static final long OCCUPANCY_TIME_MS = 1000*60*15;

	protected void updateAccessTime() {
		lastAccessTime = System.currentTimeMillis();
	}

	public AbstractCachingContentAPI() {
		super();
	}

	@Override
	public boolean canEvict() {
		return System.currentTimeMillis() > lastAccessTime + OCCUPANCY_TIME_MS;
	}

}