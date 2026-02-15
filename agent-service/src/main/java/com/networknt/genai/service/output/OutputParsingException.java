package com.networknt.genai.service.output;

import com.networknt.genai.exception.LangChain4jException;

public class OutputParsingException extends LangChain4jException {

    public OutputParsingException(String message, Throwable cause) {
        super(message, cause);
    }
}
