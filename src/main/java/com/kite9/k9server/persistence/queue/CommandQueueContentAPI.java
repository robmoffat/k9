package com.kite9.k9server.persistence.queue;

import java.io.InputStream;
import java.util.EnumSet;
import java.util.function.Consumer;

import com.kite9.k9server.adl.holder.ADL;
import com.kite9.k9server.command.content.HasOperations;
import com.kite9.k9server.persistence.cache.AbstractCachingContentAPI;
import com.kite9.k9server.persistence.cache.back.VersionedContentAPI;
import com.kite9.k9server.persistence.cache.front.InputStreamCache;
import com.kite9.k9server.persistence.cache.front.UpdateableContentAPI;

/**
 * Writes operations to the ChangeQueue, as well as making them on the 
 * delegate.
 * 
 * @author robmoffat
 */
public class CommandQueueContentAPI<K> extends AbstractCachingContentAPI<K> {

	private ChangeQueue<K> cq;
	private VersionedContentAPI<K> backingStore;
	private UpdateableContentAPI<K> frontCache;
	
	public CommandQueueContentAPI(UpdateableContentAPI<K> frontCache, ChangeQueue<K> cq, VersionedContentAPI<K> backingStore) {
		this.frontCache = frontCache;
		this.cq = cq;
		this.backingStore = backingStore;
	}

	@Override
	public void commitRevision(byte[] contents, String message) {
		Consumer<K> c = frontCache.commitRevision(contents, message);
		cq.addItem(new ChangeQueue.Commit<K>(backingStore, message, contents, c));
	}

	@Override
	public void commitRevision(String contents, String message) {
		Consumer<K> c = frontCache.commitRevision(contents, message);
		cq.addItem(new ChangeQueue.Commit<K>(backingStore, message, contents, c));
	}

	@Override
	public InputStream undo() {
		if (getOperations().contains(HasOperations.Operation.UNDO)) {
			InputStreamCache<K> isc = frontCache.undo();
			cq.addItem(new ChangeQueue.Move<K>(backingStore, isc.getRevisionID()));
			return isc.get();
		} else {
			return getCurrentRevisionContent();
		}
	}

	@Override
	public InputStream redo() {
		if (getOperations().contains(HasOperations.Operation.REDO)) {
			InputStreamCache<K> isc = frontCache.redo();
			cq.addItem(new ChangeQueue.Move<K>(backingStore, isc.getRevisionID()));
			return isc.get();
		} else {
			return getCurrentRevisionContent();
		}
	}

	@Override
	public void addMeta(ADL adl) {
		super.addMeta(adl);
		adl.setMeta("commits", ""+cq.getQueueSize());
	}

	@Override
	public void update() {
		frontCache.update();
	}

	@Override
	public EnumSet<Operation> getOperations() {
		return frontCache.getOperations();
	}

	@Override
	public InputStream getCurrentRevisionContent() {
		return frontCache.getCurrentRevisionContent().get();
	}
}
