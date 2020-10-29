package com.example.restwithoutframeworks.exceptions;

public class EndpointNotFoundException extends RuntimeException {
	public EndpointNotFoundException() {
		super();
	}

	public EndpointNotFoundException(String message) {
		super(message);
	}
}
