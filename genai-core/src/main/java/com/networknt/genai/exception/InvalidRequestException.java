package com.networknt.genai.exception;

/**
 * Thrown when a request is invalid.
 */
public class InvalidRequestException extends NonRetriableException {
    /**
     * Creates a new invalid request exception.
     *
     * @param message the error message
     */
    public InvalidRequestException(String message) {
        super(message);
    }

    /**
     * Creates a new invalid request exception.
     *
     * @param cause the cause
     */
    public InvalidRequestException(Throwable cause) {
        this(cause.getMessage(), cause);
    }

    /**
     * Creates a new invalid request exception.
     *
     * @param message the error message
     * @param cause   the cause
     */
    public InvalidRequestException(String message, Throwable cause) {
        super(message, cause);
    }
}
