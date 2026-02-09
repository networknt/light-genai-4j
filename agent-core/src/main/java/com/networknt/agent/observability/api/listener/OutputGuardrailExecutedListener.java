package com.networknt.agent.observability.api.listener;

import com.networknt.agent.guardrail.OutputGuardrail;
import com.networknt.agent.guardrail.OutputGuardrailRequest;
import com.networknt.agent.guardrail.OutputGuardrailResult;
import com.networknt.agent.observability.api.event.OutputGuardrailExecutedEvent;

/**
 * An event listener specifically designed to handle {@link OutputGuardrailExecutedEvent}.
 * This listener provides a mechanism for processing events that occur during the execution
 * of output guardrail validations.
 *
 * The purpose of this interface is to specialize the generic {@link GuardrailExecutedListener}
 * for use with output-related guardrail operations. These operations validate outputs from an LLM
 * against predefined criteria, encapsulated within the {@code OutputGuardrail}.
 */
@FunctionalInterface
public non-sealed interface OutputGuardrailExecutedListener
        extends GuardrailExecutedListener<
                OutputGuardrailExecutedEvent, OutputGuardrailRequest, OutputGuardrailResult, OutputGuardrail> {
    @Override
    default Class<OutputGuardrailExecutedEvent> getEventClass() {
        return OutputGuardrailExecutedEvent.class;
    }
}
