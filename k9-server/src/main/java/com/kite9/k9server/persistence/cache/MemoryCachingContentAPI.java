package com.kite9.k9server.persistence.cache;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.EnumSet;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.apache.commons.io.Charsets;
import org.kite9.framework.common.Kite9ProcessingException;
import org.springframework.util.StreamUtils;

import com.kite9.k9server.adl.holder.ADL;
import com.kite9.k9server.command.content.ContentAPI;

public class MemoryCachingContentAPI<K> implements ContentAPI {
	
	public interface HasInputStream extends Supplier<InputStream>{}
	
	protected String path;
	private List<HasInputStream> versions;
	private AtomicInteger currentVersion;
	private VersionedContentAPI<K> back;
	
	public MemoryCachingContentAPI(String path, VersionedContentAPI<K> back) {
		this.path = path;
		this.back = back;
		setup(back);
	}
	
	protected HasInputStream createHolder(byte[] contents) {
		return () ->  new ByteArrayInputStream(contents);
	}
	
	protected HasInputStream createHolder(String contents) {
		return createHolder(contents.getBytes(Charsets.UTF_8));
	}

	protected HasInputStream buildOnDemandCache(K k, VersionedContentAPI<K> backing) {

		return new HasInputStream() {
			
			private byte[] contents;
			
			@Override
			public InputStream get() {
				if (contents == null) {
					try {
						contents = StreamUtils.copyToByteArray(backing.getVersionContent(k));
					} catch (IOException e) {
						throw new Kite9ProcessingException("Couldn't load contents for "+k, e);
					}
				}
				
				return new ByteArrayInputStream(contents);
			}
		};
		
	}

	protected void setup(VersionedContentAPI<K> backing) {
		List<K> allVersions = backing.getVersionHistory();
		K currentK = backing.getCurrentVersion();
		currentVersion = new AtomicInteger(allVersions.indexOf(currentK));
		versions = allVersions.stream()
				.map(k -> buildOnDemandCache(k, backing))
				.collect(Collectors.toList());
	}	
		
	@Override
	public InputStream getCurrentRevisionContent() {
		return versions.get(currentVersion.get()).get();
	}

	@Override
	public void commitRevision(byte[] contents, String message) {
		versions.add(0, createHolder(contents));
		currentVersion.set(0);
	}

	@Override
	public void commitRevision(String contents, String message) {
		versions.add(0, createHolder(contents));
		currentVersion.set(0);
	}

	@Override
	public InputStream undo() {
		currentVersion.getAndUpdate(i -> Math.min(i+1, versions.size() - 1));
		return getCurrentRevisionContent();
	}

	@Override
	public InputStream redo() {
		currentVersion.getAndUpdate(i -> Math.max(i-1, 0));
		return getCurrentRevisionContent();
	}

	@Override
	public EnumSet<Operation> getOperations() {
		EnumSet<Operation> out = EnumSet.of(Operation.COMMIT);
		int cv = currentVersion.get();
		
		if (cv > 0) {
			out.add(Operation.REDO);
		}
		
		if (cv < versions.size() - 1) {
			out.add(Operation.UNDO);
		}
		
		return out;
	}

	@Override
	public ContentAPI withPath(String ext) {
		return new MemoryCachingContentAPI<K>(path + ext, back.withPath(ext));
	}

	@Override
	public void addMeta(ADL adl) {
		ContentAPI.super.addMeta(adl);
		adl.setMeta("revision", ""+currentVersion);
	}

	
}
