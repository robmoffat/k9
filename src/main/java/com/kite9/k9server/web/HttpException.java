package com.kite9.k9server.web;

import org.springframework.http.HttpStatus;

public class HttpException extends RuntimeException {
	
	private final HttpStatus status;

	public HttpStatus getStatus() {
		return status;
	}

	public HttpException(HttpStatus status) {
		super();
		this.status = status;
	}

	public HttpException(HttpStatus status, String message, Throwable cause) {
		super(message, cause);
		this.status = status;
	}

	public HttpException(HttpStatus status, String message) {
		super(message);
		this.status = status;
	}
	
	public HttpException(String message, HttpStatus status) {
		super(message);
		this.status = status;
	}
}
