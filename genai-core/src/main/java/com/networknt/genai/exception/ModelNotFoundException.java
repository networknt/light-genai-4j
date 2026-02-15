package com.networknt.genai.exception;

/**
 * Thrown when a model is not found.
 */
public class ModelNotFoundException extends NonRetriableException {
    /**
     * Creates a new model not found exception.
     *
     * @param message the error message
     */
    public ModelNotFoundException(String message) {
        super(message);
    }

    /**
     * Creates a new model not found exception.
     *
     * @param cause the cause
     */
    public ModelNotFoundException(Throwable cause) {
        this(cause.getMessage(), cause);
    }

    /**
     * Creates a new model not found exception.
     *
     * @param message the error message
     * @param cause   the cause
     */
    public ModelNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
