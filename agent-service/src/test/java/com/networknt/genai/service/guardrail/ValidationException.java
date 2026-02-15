package com.networknt.genai.service.guardrail;

public class ValidationException extends RuntimeException {
    public ValidationException(String message) {
        super(message);
    }
}
