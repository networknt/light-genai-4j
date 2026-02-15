package com.networknt.genai.model;

import com.networknt.genai.exception.LangChain4jException;

/**
 * An exception thrown by a model that could be disabled by a user.
 */
public class ModelDisabledException extends LangChain4jException {

    public ModelDisabledException(String message) {
        super(message);
    }
}
