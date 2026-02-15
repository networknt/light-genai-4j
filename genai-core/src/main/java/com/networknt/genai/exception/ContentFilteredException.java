package com.networknt.genai.exception;

/**
 * Exception thrown when the LLM provider refuses to process a request due to content filtering
 * or violation of usage policies.
 * <p>
 * This typically indicates that the input was flagged as inappropriate, unsafe, or against
 * the provider’s content guidelines.
 *
 * @since 1.2.0
 */
public class ContentFilteredException extends InvalidRequestException {

    /**
     * Creates a new content filtered exception.
     *
     * @param message the error message
     */
    public ContentFilteredException(String message) {
        super(message);
    }

    /**
     * Creates a new content filtered exception.
     *
     * @param cause the cause
     */
    public ContentFilteredException(Throwable cause) {
        super(cause);
    }

    /**
     * Creates a new content filtered exception.
     *
     * @param message the error message
     * @param cause   the cause
     */
    public ContentFilteredException(String message, Throwable cause) {
        super(message, cause);
    }
}
