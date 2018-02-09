package com.kite9.k9server.command;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class CommandException extends Exception {

	private Command c;
	
	public CommandException(String message, Throwable cause, Command c) {
		super(message, cause);
		this.c = c;
	}

	public CommandException(String message, Command c) {
		super(message);
		this.c = c;
	}

	@Override
	public String getMessage() {
		try {
			return super.getMessage()+" - Command: "+new ObjectMapper().writeValueAsString(c);
		} catch (JsonProcessingException e) {
			return super.getMessage()+" - Command couldn't be serialized";
		}
	}

	
	
}
