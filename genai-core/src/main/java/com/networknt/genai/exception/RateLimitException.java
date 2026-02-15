package com.networknt.genai.exception;

/**
 * Thrown when a rate limit is exceeded.
 */
public class RateLimitException extends RetriableException {
    /**
     * Creates a new rate limit exception.
     *
     * @param message the error message
     */
    public RateLimitException(String message) {
        super(message);
    }

    /**
     * Creates a new rate limit exception.
     *
     * @param cause the cause
     */
    public RateLimitException(Throwable cause) {
        this(cause.getMessage(), cause);
    }

    /**
     * Creates a new rate limit exception.
     *
     * @param message the error message
     * @param cause   the cause
     */
    public RateLimitException(String message, Throwable cause) {
        super(message, cause);
    }
}
