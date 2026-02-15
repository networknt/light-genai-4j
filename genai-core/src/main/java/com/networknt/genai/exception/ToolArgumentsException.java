package com.networknt.genai.exception;

/**
 * Indicates that something is wrong with the tool arguments.
 * For example, the JSON cannot be parsed, or an argument is of the wrong type.
 *
 * @since 1.4.0
 */
public class ToolArgumentsException extends LangChain4jException {

    private final Integer errorCode;

    /**
     * Creates a new tool arguments exception.
     *
     * @param message the error message
     */
    public ToolArgumentsException(String message) {
        this(message, null);
    }

    /**
     * Creates a new tool arguments exception.
     *
     * @param cause the cause
     */
    public ToolArgumentsException(Throwable cause) {
        this(cause, null);
    }

    /**
     * Creates a new tool arguments exception.
     *
     * @param message   the error message
     * @param errorCode the error code
     */
    public ToolArgumentsException(String message, Integer errorCode) {
        this(new RuntimeException(message), errorCode);
    }

    /**
     * Creates a new tool arguments exception.
     *
     * @param cause     the cause
     * @param errorCode the error code
     */
    public ToolArgumentsException(Throwable cause, Integer errorCode) {
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
