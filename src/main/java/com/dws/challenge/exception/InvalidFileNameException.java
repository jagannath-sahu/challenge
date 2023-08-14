package com.dws.challenge.exception;

public class InvalidFileNameException extends RuntimeException {
    private static final long serialVersionUID = 1L;

	public InvalidFileNameException(String message) {
        super(message);
    }
}
