package com.example.restwithoutframeworks.exceptions;

public class RequestMethodNotImplementedException extends RuntimeException {
	public RequestMethodNotImplementedException() {
		super();
	}

	public RequestMethodNotImplementedException(String message) {
		super(message);
	}
}
