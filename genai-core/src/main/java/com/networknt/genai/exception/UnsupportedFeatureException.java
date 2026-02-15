package com.networknt.genai.exception;

/**
 * Thrown when a feature is not supported.
 */
public class UnsupportedFeatureException extends LangChain4jException {

    /**
     * Creates a new unsupported feature exception.
     *
     * @param message the error message
     */
    public UnsupportedFeatureException(String message) {
        super(message);
    }
}
