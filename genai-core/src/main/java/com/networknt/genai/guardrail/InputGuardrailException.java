package com.networknt.genai.guardrail;

/**
 * Exception thrown when an input guardrail validation fails.
 * <p>
 *     This class is not intended to be thrown within guardrail implementations. It is for the framework only. It is ok to catch it.
 * </p>
 */
public final class InputGuardrailException extends GuardrailException {
    /**
     * Creates a new input guardrail exception.
     *
     * @param message the message
     */
    public InputGuardrailException(String message) {
        super(message);
    }

    /**
     * Creates a new input guardrail exception.
     *
     * @param message the message
     * @param cause   the cause
     */
    public InputGuardrailException(String message, Throwable cause) {
        super(message, cause);
    }
}
