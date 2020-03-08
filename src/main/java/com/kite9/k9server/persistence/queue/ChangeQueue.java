package com.kite9.k9server.persistence.queue;

import java.util.function.Consumer;

import com.kite9.k9server.persistence.cache.back.VersionedContentAPI;

public interface ChangeQueue<K> {
	
	public abstract class Change<K> {

		VersionedContentAPI<K> on;
		
		abstract void perform();
		
	}
		
	public class Commit<K> extends Change<K> {

		String message;
		Object payload;
		Consumer<K> revisionIDConsumer;
		
		public Commit(VersionedContentAPI<K> on, String message, Object payload, Consumer<K> revisionIDConsumer) {
			super();
			this.on = on;
			this.message = message;
			this.payload = payload;
			this.revisionIDConsumer = revisionIDConsumer;
		}

		@Override
		void perform() {
			if (payload instanceof String) {
				K k = on.commitRevision((String) payload, message);
				revisionIDConsumer.accept(k);
			} else {
				K k = on.commitRevision((byte[]) payload, message);
				revisionIDConsumer.accept(k);
			}
		}
	}
	
	public class Move<K> extends Change<K> {
		
		K revision;
		
		public Move(VersionedContentAPI<K> on, K revisionID) {
			this.on = on;
			this.revision = revisionID;
		}

		@Override
		void perform() {
			on.updateCurrentRevision(revision);
		}
	}


	public int getQueueSize();
	
	public void addItem(Change<K> c);
}
