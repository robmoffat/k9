package com.kite9.k9server.persistence.queue;

import com.kite9.k9server.command.content.ContentAPI;
import com.kite9.k9server.command.content.ContentAPI.Operation;

public interface ChangeQueue {
	
	public static class Change {
		
		ContentAPI on;
		Operation o;
		String message;
		Object payload;
		
		public Change(Operation o, ContentAPI on, String message, Object payload) {
			super();
			this.o = o;
			this.on = on;
			this.message = message;
			this.payload = payload;
		}

		public Change(Operation o, ContentAPI backingStore) {
			this.o = o;
			this.on = backingStore;
		}
	}

	public int getQueueSize();
	
	public void addItem(Change c);
}
