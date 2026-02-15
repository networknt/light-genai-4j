package com.networknt.genai.exception;

/**
 * Thrown when an internal server error occurs.
 */
public class InternalServerException extends RetriableException {
    /**
     * Creates a new internal server exception.
     *
     * @param message the error message
     */
    public InternalServerException(String message) {
        super(message);
    }

    /**
     * Creates a new internal server exception.
     *
     * @param cause the cause
     */
    public InternalServerException(Throwable cause) {
        this(cause.getMessage(), cause);
    }

    /**
     * Creates a new internal server exception.
     *
     * @param message the error message
     * @param cause   the cause
     */
    public InternalServerException(String message, Throwable cause) {
        super(message, cause);
    }
}
