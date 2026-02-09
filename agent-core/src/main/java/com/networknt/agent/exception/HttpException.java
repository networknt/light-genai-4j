package com.networknt.agent.exception;

public class HttpException extends LangChain4jException {

    private final int statusCode;

    public HttpException(int statusCode, String message) {
        super(message);
        this.statusCode = statusCode;
    }

    public int statusCode() {
        return statusCode;
    }
}
