package com.networknt.genai.exception;

/**
 * Thrown when a timeout occurs.
 */
public class TimeoutException extends RetriableException {
    /**
     * Creates a new timeout exception.
     *
     * @param message the error message
     */
    public TimeoutException(String message) {
        super(message);
    }

    /**
     * Creates a new timeout exception.
     *
     * @param cause the cause
     */
    public TimeoutException(Throwable cause) {
        this(cause.getMessage(), cause);
    }

    /**
     * Creates a new timeout exception.
     *
     * @param message the error message
     * @param cause   the cause
     */
    public TimeoutException(String message, Throwable cause) {
        super(message, cause);
    }
}
