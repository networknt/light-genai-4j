package com.networknt.genai.exception;

/**
 * Thrown when a non-retriable error occurs.
 */
public class NonRetriableException extends LangChain4jException {
    /**
     * Creates a new non-retriable exception.
     *
     * @param message the error message
     */
    public NonRetriableException(String message) {
        super(message);
    }

    /**
     * Creates a new non-retriable exception.
     *
     * @param cause the cause
     */
    public NonRetriableException(Throwable cause) {
        this(cause.getMessage(), cause);
    }

    /**
     * Creates a new non-retriable exception.
     *
     * @param message the error message
     * @param cause   the cause
     */
    public NonRetriableException(String message, Throwable cause) {
        super(message, cause);
    }
}
