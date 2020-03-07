package com.kite9.k9server.persistence.queue;

import java.io.InputStream;

import com.kite9.k9server.adl.holder.ADL;
import com.kite9.k9server.command.content.ContentAPI;
import com.kite9.k9server.persistence.cache.DelegatingContentAPI;
import com.kite9.k9server.persistence.queue.ChangeQueue.Change;

/**
 * Writes operations to the ChangeQueue, as well as making them on the 
 * delegate.
 * 
 * @author robmoffat
 */
public class CommandQueueContentAPI extends DelegatingContentAPI {

	private ChangeQueue cq;
	private ContentAPI backingStore;
	
	public CommandQueueContentAPI(ContentAPI frontCache, ChangeQueue cq, ContentAPI backingStore) {
		super(frontCache);
		this.cq = cq;
		this.backingStore = backingStore;
	}

	@Override
	public void commitRevision(byte[] contents, String message) {
		super.commitRevision(contents, message);
		cq.addItem(new ChangeQueue.Change(Operation.COMMIT, backingStore, message, contents));
	}

	@Override
	public void commitRevision(String contents, String message) {
		super.commitRevision(contents, message);
		cq.addItem(new Change(Operation.COMMIT, backingStore, message, contents));
	}

	@Override
	public InputStream undo() {
		if (getOperations().contains(Operation.UNDO)) {
			cq.addItem(new Change(Operation.UNDO, backingStore));
			return super.undo();
		} else {
			return getCurrentRevisionContent();
		}
	}

	@Override
	public InputStream redo() {
		if (getOperations().contains(Operation.REDO)) {
			cq.addItem(new Change(Operation.REDO, backingStore));
			return super.redo();
		} else {
			return getCurrentRevisionContent();
		}
	}

	@Override
	public ContentAPI withPath(String ext) {
		return new CommandQueueContentAPI(super.withPath(ext), cq, backingStore.withPath(ext));
	}

	@Override
	public void addMeta(ADL adl) {
		super.addMeta(adl);
		adl.setMeta("commits", ""+cq.getQueueSize());
	}

	
	
}
