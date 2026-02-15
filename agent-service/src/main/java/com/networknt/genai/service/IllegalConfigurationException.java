package com.networknt.genai.service;

import com.networknt.genai.exception.LangChain4jException;

import static java.lang.String.format;

public class IllegalConfigurationException extends LangChain4jException {

    public IllegalConfigurationException(String message) {
        super(message);
    }

    public static IllegalConfigurationException illegalConfiguration(String message) {
        return new IllegalConfigurationException(message);
    }

    public static IllegalConfigurationException illegalConfiguration(String format, Object... args) {
        return new IllegalConfigurationException(format(format, args));
    }
}
