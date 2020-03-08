package com.kite9.k9server.persistence.cache.front;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.util.EnumSet;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.apache.commons.io.Charsets;
import org.kite9.framework.common.Kite9ProcessingException;
import org.springframework.util.StreamUtils;

import com.kite9.k9server.adl.holder.ADL;
import com.kite9.k9server.command.content.HasOperations;
import com.kite9.k9server.persistence.cache.Caching;
import com.kite9.k9server.persistence.cache.back.VersionedContentAPI;

public class MemoryCachingContentAPI<K> implements UpdateableContentAPI<K>, Caching {
	
	protected String path;
	private List<InputStreamCache<K>> versions;
	private AtomicInteger currentVersion;
	private VersionedContentAPI<K> back;
	
	public MemoryCachingContentAPI(String path, VersionedContentAPI<K> back) {
		this.path = path;
		this.back = back;
		update();
	}
	
	protected InputStreamCache<K> createHolder(byte[] contents) {
		return new InputStreamCache<K>() {
			
			@Override
			public InputStream get() {
				return new ByteArrayInputStream(contents);
			}
		}; 
	}
	
	protected InputStreamCache<K> createHolder(String contents) {
		return createHolder(contents.getBytes(Charsets.UTF_8));
	}

	protected InputStreamCache<K> buildOnDemandCache(K k, VersionedContentAPI<K> backing) {

		return new InputStreamCache<K>(k) {
			
			private SoftReference<byte[]> contents;
			
			@Override
			public InputStream get() {
				if ((contents == null) || (contents.get() == null)) {
					try {
						contents = new SoftReference<byte[]>(StreamUtils.copyToByteArray(backing.getVersionContent(k)));
					} catch (IOException e) {
						throw new Kite9ProcessingException("Couldn't load contents for "+k, e);
					}
				}
				
				return new ByteArrayInputStream(contents.get());
			}
		};
		
	}

	public void update() {
		List<K> allVersions = this.back.getVersionHistory();
		K currentK = this.back.getCurrentRevisionID();
		currentVersion = new AtomicInteger(allVersions.indexOf(currentK));
		versions = allVersions.stream()
				.map(k -> buildOnDemandCache(k, this.back))
				.collect(Collectors.toList());
	}	
		
	@Override
	public InputStreamCache<K> getCurrentRevisionContent() {
		return versions.get(currentVersion.get());
	}

	@Override
	public Consumer<K> commitRevision(byte[] contents, String message) {
		InputStreamCache<K> h = createHolder(contents);
		versions.add(0, h);
		currentVersion.set(0);
		return h;
	}

	@Override
	public Consumer<K> commitRevision(String contents, String message) {
		InputStreamCache<K> h = createHolder(contents);
		versions.add(0, h);
		currentVersion.set(0);
		return h;
	}

	@Override
	public InputStreamCache<K> undo() {
		currentVersion.getAndUpdate(i -> Math.min(i+1, versions.size() - 1));
		return getCurrentRevisionContent();
	}

	@Override
	public InputStreamCache<K> redo() {
		currentVersion.getAndUpdate(i -> Math.max(i-1, 0));
		return getCurrentRevisionContent();
	}

	@Override
	public EnumSet<HasOperations.Operation> getOperations() {
		EnumSet<HasOperations.Operation> out = EnumSet.of(HasOperations.Operation.COMMIT);
		int cv = currentVersion.get();
		
		if (cv > 0) {
			out.add(HasOperations.Operation.REDO);
		}
		
		if (cv < versions.size() - 1) {
			out.add(HasOperations.Operation.UNDO);
		}
		
		return out;
	}

	@Override
	public void addMeta(ADL adl) {
		UpdateableContentAPI.super.addMeta(adl);
		adl.setMeta("revision", ""+currentVersion);
	}

	@Override
	public boolean canEvict() {
		return true;
	}

}
