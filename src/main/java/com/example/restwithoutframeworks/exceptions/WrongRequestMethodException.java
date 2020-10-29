package com.example.restwithoutframeworks.exceptions;

public class WrongRequestMethodException extends Exception {
	public WrongRequestMethodException() {
		super();
	}

	public WrongRequestMethodException(String message) {
		super(message);
	}
}
