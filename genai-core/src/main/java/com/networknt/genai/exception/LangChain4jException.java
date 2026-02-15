package com.networknt.genai.exception;

/**
 * Base exception for all LangChain4j exceptions.
 */
public class LangChain4jException extends RuntimeException {

    /**
     * Creates a new exception.
     *
     * @param message the error message
     */
    public LangChain4jException(String message) {
        super(message);
    }

    /**
     * Creates a new exception.
     *
     * @param cause the cause
     */
    public LangChain4jException(Throwable cause) {
        this(cause.getMessage(), cause);
    }

    /**
     * Creates a new exception.
     *
     * @param message the error message
     * @param cause   the cause
     */
    public LangChain4jException(String message, Throwable cause) {
        super(message, cause);
    }
}
