package com.kite9.k9server.command;

import java.util.Collections;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * This is thrown when something goes wrong in command processing.
 */
@ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
public class CommandException extends RuntimeException {

	private List<Command> c;
	
	public CommandException(String message, Throwable cause, Command c) {
		super(message, cause);
		this.c = Collections.singletonList(c);
	}

	public CommandException(String message) {
		super(message);
	}
	
	public CommandException(String message, Command c) {
		super(message);
		this.c = Collections.singletonList(c);
	}

	public CommandException(String message, List<Command> c) {
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
