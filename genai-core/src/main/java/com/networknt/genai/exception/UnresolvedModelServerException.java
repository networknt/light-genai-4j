package com.networknt.genai.exception;

/**
 * Thrown when a model server URL cannot be resolved.
 */
public class UnresolvedModelServerException extends NonRetriableException {
    /**
     * Creates a new unresolved model server exception.
     *
     * @param message the error message
     */
    public UnresolvedModelServerException(String message) {
        super(message);
    }

    /**
     * Creates a new unresolved model server exception.
     *
     * @param cause the cause
     */
    public UnresolvedModelServerException(Throwable cause) {
        this(cause.getMessage(), cause);
    }

    /**
     * Creates a new unresolved model server exception.
     *
     * @param message the error message
     * @param cause   the cause
     */
    public UnresolvedModelServerException(String message, Throwable cause) {
        super(message, cause);
    }
}
