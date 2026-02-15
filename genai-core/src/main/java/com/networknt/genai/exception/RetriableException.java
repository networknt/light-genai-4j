package com.networknt.genai.exception;

/**
 * Thrown when a retriable error occurs.
 */
public class RetriableException extends LangChain4jException {
    /**
     * Creates a new retriable exception.
     *
     * @param message the error message
     */
    public RetriableException(String message) {
        super(message);
    }

    /**
     * Creates a new retriable exception.
     *
     * @param cause the cause
     */
    public RetriableException(Throwable cause) {
        this(cause.getMessage(), cause);
    }

    /**
     * Creates a new retriable exception.
     *
     * @param message the error message
     * @param cause   the cause
     */
    public RetriableException(String message, Throwable cause) {
        super(message, cause);
    }
}
