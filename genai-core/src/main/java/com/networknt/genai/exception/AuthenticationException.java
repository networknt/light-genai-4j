package com.networknt.genai.exception;

/**
 * Thrown when authentication fails.
 */
public class AuthenticationException extends NonRetriableException {
    /**
     * Creates a new authentication exception.
     *
     * @param message the error message
     */
    public AuthenticationException(String message) {
        super(message);
    }

    /**
     * Creates a new authentication exception.
     *
     * @param cause the cause
     */
    public AuthenticationException(Throwable cause) {
        this(cause.getMessage(), cause);
    }

    /**
     * Creates a new authentication exception.
     *
     * @param message the error message
     * @param cause   the cause
     */
    public AuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }
}
