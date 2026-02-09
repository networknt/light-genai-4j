package com.networknt.agent.observability.event;

import com.networknt.agent.guardrail.OutputGuardrail;
import com.networknt.agent.guardrail.OutputGuardrailRequest;
import com.networknt.agent.guardrail.OutputGuardrailResult;
import com.networknt.agent.observability.api.event.OutputGuardrailExecutedEvent;

/**
 * Default implementation of {@link OutputGuardrailExecutedEvent}.
 */
public class DefaultOutputGuardrailExecutedEvent
        extends DefaultGuardrailExecutedEvent<
                OutputGuardrailRequest, OutputGuardrailResult, OutputGuardrail, OutputGuardrailExecutedEvent>
        implements OutputGuardrailExecutedEvent {

    public DefaultOutputGuardrailExecutedEvent(OutputGuardrailExecutedEventBuilder builder) {
        super(builder);
    }
}
