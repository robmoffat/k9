package com.kite9.k9server.persistence.cache;

import java.io.InputStream;
import java.util.EnumSet;

import com.kite9.k9server.command.content.ContentAPI;

public class DelegatingContentAPI implements ContentAPI {

	private ContentAPI delegate;

	public DelegatingContentAPI(ContentAPI delegate) {
		super();
		this.delegate = delegate;
	}

	public InputStream getCurrentRevisionContent() {
		return delegate.getCurrentRevisionContent();
	}

	public void commitRevision(byte[] contents, String message) {
		delegate.commitRevision(contents, message);
	}

	public void commitRevision(String contents, String message) {
		delegate.commitRevision(contents, message);
	}

	public InputStream undo() {
		return delegate.undo();
	}

	public InputStream redo() {
		return delegate.redo();
	}

	public ContentAPI withPath(String ext) {
		return delegate.withPath(ext);
	}

	public EnumSet<Operation> getOperations() {
		return delegate.getOperations();
	}
	
	
}
