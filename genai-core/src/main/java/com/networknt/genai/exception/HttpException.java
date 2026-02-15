package com.networknt.genai.exception;

/**
 * Thrown when an HTTP error occurs.
 */
public class HttpException extends LangChain4jException {

    private final int statusCode;

    /**
     * Creates a new HTTP exception.
     *
     * @param statusCode the status code
     * @param message    the error message
     */
    public HttpException(int statusCode, String message) {
        super(message);
        this.statusCode = statusCode;
    }

    /**
     * Returns the status code.
     *
     * @return the status code
     */
    public int statusCode() {
        return statusCode;
    }
}
