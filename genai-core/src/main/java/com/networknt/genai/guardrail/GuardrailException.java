package com.networknt.genai.guardrail;

import com.networknt.genai.exception.LangChain4jException;

/**
 * Exception thrown when an input or output guardrail validation fails.
 * <p>
 *     This class is not intended to be used within guardrail implementations. It is for the framework only.
 * </p>
 * @see InputGuardrailException
 * @see OutputGuardrailException
 */
public sealed class GuardrailException extends LangChain4jException
        permits InputGuardrailException, OutputGuardrailException {
    /**
     * Creates a new guardrail exception.
     *
     * @param message the message
     */
    protected GuardrailException(String message) {
        super(message);
    }

    /**
     * Creates a new guardrail exception.
     *
     * @param message the message
     * @param cause   the cause
     */
    protected GuardrailException(String message, Throwable cause) {
        super(message, cause);
    }
}
