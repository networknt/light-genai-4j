package com.networknt.agent.exception;

public class NonRetriableException extends LangChain4jException {
    public NonRetriableException(String message) {
        super(message);
    }

    public NonRetriableException(Throwable cause) {
        this(cause.getMessage(), cause);
    }

    public NonRetriableException(String message, Throwable cause) {
        super(message, cause);
    }
}
