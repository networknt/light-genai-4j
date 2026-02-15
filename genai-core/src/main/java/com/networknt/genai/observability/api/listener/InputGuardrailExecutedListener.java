package com.networknt.genai.observability.api.listener;

import com.networknt.genai.guardrail.InputGuardrail;
import com.networknt.genai.guardrail.InputGuardrailRequest;
import com.networknt.genai.guardrail.InputGuardrailResult;
import com.networknt.genai.observability.api.event.InputGuardrailExecutedEvent;

/**
 * A specialized listener interface for handling events of type {@link InputGuardrailExecutedEvent},
 * which are triggered upon the execution of input guardrail validations. This listener provides
 * functionality specific to input-based guardrail execution, including access to the corresponding
 * input request, result, and guardrail implementation.
 */
@FunctionalInterface
public non-sealed interface InputGuardrailExecutedListener
        extends GuardrailExecutedListener<
                InputGuardrailExecutedEvent, InputGuardrailRequest, InputGuardrailResult, InputGuardrail> {
    @Override
    default Class<InputGuardrailExecutedEvent> getEventClass() {
        return InputGuardrailExecutedEvent.class;
    }
}
