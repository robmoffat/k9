package com.kite9.k9server.command;

import com.kite9.k9server.domain.Document;
import com.kite9.k9server.domain.User;

public abstract class AbstractCommand implements Command {
	
	public AbstractCommand(Document d, User author) {
		super();
		this.d = d;
		this.author = author;
	}

	private final Document d;
	private final User author;

	public User getAuthor() {
		return author;
	}
	
	@Override
	public Document getDocument() {
		return d;
	}

}
