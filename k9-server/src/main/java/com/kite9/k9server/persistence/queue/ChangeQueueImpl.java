package com.kite9.k9server.persistence.queue;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import org.kite9.framework.common.Kite9ProcessingException;

public class ChangeQueueImpl<K> implements ChangeQueue<K> {
	
	private final BlockingQueue<Change<K>> workQueue;
    private final ExecutorService service;

    public ChangeQueueImpl(int workQueueSize) {
        this.workQueue = new LinkedBlockingQueue<>(workQueueSize);
        this.service = Executors.newFixedThreadPool(1);
    }


	@Override
	public int getQueueSize() {
		return workQueue.size();
	}

	@Override
	public void addItem(Change<K> c) {
		try {
			workQueue.put(c);
	        service.submit(createWorker());
		} catch (InterruptedException e) {
			throw new Kite9ProcessingException("Work Queue Put Interrupted "+c, e);
		}
	}
	
	public Runnable createWorker() {
		return () -> {
			while (workQueue.size() > 0) {
				Change<K> c = workQueue.poll();
				c.perform();
			}
		};
	}

}
