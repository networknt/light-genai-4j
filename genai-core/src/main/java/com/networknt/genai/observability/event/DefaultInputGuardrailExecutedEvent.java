package com.networknt.genai.observability.event;

import com.networknt.genai.data.message.UserMessage;
import com.networknt.genai.guardrail.InputGuardrail;
import com.networknt.genai.guardrail.InputGuardrailRequest;
import com.networknt.genai.guardrail.InputGuardrailResult;
import com.networknt.genai.observability.api.event.InputGuardrailExecutedEvent;

/**
 * Default implementation of {@link InputGuardrailExecutedEvent}.
 */
public class DefaultInputGuardrailExecutedEvent
        extends DefaultGuardrailExecutedEvent<
                InputGuardrailRequest, InputGuardrailResult, InputGuardrail, InputGuardrailExecutedEvent>
        implements InputGuardrailExecutedEvent {

    /**
     * Creates a new instance.
     *
     * @param builder the builder
     */
    public DefaultInputGuardrailExecutedEvent(InputGuardrailExecutedEventBuilder builder) {
        super(builder);
    }

    /**
     * Returns the rewritten user message.
     *
     * @return the rewritten user message
     */
    @Override
    public UserMessage rewrittenUserMessage() {
        return result().userMessage(request());
    }
}
