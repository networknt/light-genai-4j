package com.networknt.genai.observability.event;

import com.networknt.genai.guardrail.OutputGuardrail;
import com.networknt.genai.guardrail.OutputGuardrailRequest;
import com.networknt.genai.guardrail.OutputGuardrailResult;
import com.networknt.genai.observability.api.event.OutputGuardrailExecutedEvent;

/**
 * Default implementation of {@link OutputGuardrailExecutedEvent}.
 */
public class DefaultOutputGuardrailExecutedEvent
        extends DefaultGuardrailExecutedEvent<
                OutputGuardrailRequest, OutputGuardrailResult, OutputGuardrail, OutputGuardrailExecutedEvent>
        implements OutputGuardrailExecutedEvent {

    /**
     * Creates a new instance.
     *
     * @param builder the builder
     */
    public DefaultOutputGuardrailExecutedEvent(OutputGuardrailExecutedEventBuilder builder) {
        super(builder);
    }
}
