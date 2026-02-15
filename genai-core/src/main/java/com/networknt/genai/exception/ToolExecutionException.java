package com.networknt.genai.exception;

/**
 * Indicates that something went wrong while executing the tool.
 *
 * @since 1.4.0
 */
public class ToolExecutionException extends LangChain4jException {

    private final Integer errorCode;

    /**
     * Creates a new tool execution exception.
     *
     * @param message the error message
     */
    public ToolExecutionException(String message) {
        this(message, null);
    }

    /**
     * Creates a new tool execution exception.
     *
     * @param cause the cause
     */
    public ToolExecutionException(Throwable cause) {
        this(cause, null);
    }

    /**
     * Creates a new tool execution exception.
     *
     * @param message   the error message
     * @param errorCode the error code
     */
    public ToolExecutionException(String message, Integer errorCode) {
        this(new RuntimeException(message), errorCode);
    }

    /**
     * Creates a new tool execution exception.
     *
     * @param cause     the cause
     * @param errorCode the error code
     */
    public ToolExecutionException(Throwable cause, Integer errorCode) {
        super(cause);
        this.errorCode = errorCode;
    }

    /**
     * Returns the error code.
     *
     * @return the error code
     */
    public Integer errorCode() {
        return errorCode;
    }
}
