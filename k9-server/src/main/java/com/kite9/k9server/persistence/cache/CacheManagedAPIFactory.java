package com.kite9.k9server.persistence.cache;

import java.io.IOException;
import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.Authentication;

import com.kite9.k9server.command.content.ContentAPIFactory;
import com.kite9.k9server.persistence.cache.back.VersionedContentAPI;
import com.kite9.k9server.persistence.cache.front.MemoryCachingContentAPI;
import com.kite9.k9server.persistence.cache.front.UpdateableContentAPI;
import com.kite9.k9server.persistence.queue.ChangeQueue;
import com.kite9.k9server.persistence.queue.CommandQueueContentAPI;

public abstract class CacheManagedAPIFactory<K> implements ContentAPIFactory<K> {

	private static Logger logger = LoggerFactory.getLogger(CacheManagedAPIFactory.class);
	
	private final Map<String, CachingContentAPI> cache = new HashMap<>();
	private final Map<String, SoftReference<ChangeQueue<K>>> changeQueues = new HashMap<String, SoftReference<ChangeQueue<K>>>();

	public CacheManagedAPIFactory() {
		super();
	}

	@Override
	public CachingContentAPI createAPI(Authentication a, String path) throws IOException {
		CachingContentAPI out = cache.get(path);
		if (out == null) {
			out = buildNewCache(a, path);
			cache.put(path, out);
		} 
		return out;
	}

	private CachingContentAPI buildNewCache(Authentication a, String path) {
		logger.info("Building Cache For: "+path);
		VersionedContentAPI<K> backingApi = createVersionedBackingAPI(a, path);
		UpdateableContentAPI<K> front = createMemoryCachingAPI(backingApi, path);
		ChangeQueue<K> cq = createOrGetChangeQueue(a, path);
		return new CommandQueueContentAPI<K>(front, cq, backingApi);
	}

	protected ChangeQueue<K> createOrGetChangeQueue(Authentication a, String path) {
		String queueName = getQueueIdentity(a, path);
		
		SoftReference<ChangeQueue<K>> cqRef = changeQueues.get(queueName);
		ChangeQueue<K> cq = cqRef == null ? null : cqRef.get();
		
		if ((cq == null)) {
			cq = createChangeQueue(a, path);
			changeQueues.put(queueName, new SoftReference<ChangeQueue<K>>(cq));
		}
		
		return cq;
	}

	protected abstract String getQueueIdentity(Authentication a, String path);

	protected abstract ChangeQueue<K> createChangeQueue(Authentication a, String path);

	protected abstract VersionedContentAPI<K> createVersionedBackingAPI(Authentication a, String path);

	protected UpdateableContentAPI<K> createMemoryCachingAPI(VersionedContentAPI<K> with, String path) {
		return new MemoryCachingContentAPI<K>(path, with);
	}

	@Scheduled(fixedDelay = 5000)
	public void cleanUp() {
		int startSize = cache.size();
		if (startSize > 0) {
			if (cache.entrySet().removeIf(e -> e.getValue().canEvict())) {
				logger.debug("Evicting apis: "+startSize+ " -> " + cache.size());
			}
		}
		
		startSize = changeQueues.size();
		if (startSize > 0) {
			if (changeQueues.entrySet().removeIf(e -> e.getValue().get() == null)) {
				logger.debug("Evicting queues: "+startSize+ " -> " + changeQueues.size());
			}
		}
	}
	
	@Scheduled(fixedRate = 15 * 60 * 1000)
	public void update() {
		cache.values().stream()
			.filter(v -> !v.canEvict())
			.forEach(v -> v.update());
	}

}