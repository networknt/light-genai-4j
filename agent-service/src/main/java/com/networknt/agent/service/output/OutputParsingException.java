package com.networknt.agent.service.output;

import com.networknt.agent.exception.LangChain4jException;

public class OutputParsingException extends LangChain4jException {

    public OutputParsingException(String message, Throwable cause) {
        super(message, cause);
    }
}
