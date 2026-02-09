package com.networknt.agent.observability.event;

import com.networknt.agent.data.message.UserMessage;
import com.networknt.agent.guardrail.InputGuardrail;
import com.networknt.agent.guardrail.InputGuardrailRequest;
import com.networknt.agent.guardrail.InputGuardrailResult;
import com.networknt.agent.observability.api.event.InputGuardrailExecutedEvent;

/**
 * Default implementation of {@link InputGuardrailExecutedEvent}.
 */
public class DefaultInputGuardrailExecutedEvent
        extends DefaultGuardrailExecutedEvent<
                InputGuardrailRequest, InputGuardrailResult, InputGuardrail, InputGuardrailExecutedEvent>
        implements InputGuardrailExecutedEvent {

    public DefaultInputGuardrailExecutedEvent(InputGuardrailExecutedEventBuilder builder) {
        super(builder);
    }

    @Override
    public UserMessage rewrittenUserMessage() {
        return result().userMessage(request());
    }
}
