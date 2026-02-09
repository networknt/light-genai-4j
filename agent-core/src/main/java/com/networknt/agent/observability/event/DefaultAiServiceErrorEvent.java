package com.networknt.agent.observability.event;

import static com.networknt.agent.internal.ValidationUtils.ensureNotNull;

import com.networknt.agent.observability.api.event.AiServiceErrorEvent;

/**
 * Default implementation of {@link AiServiceErrorEvent}.
 */
public class DefaultAiServiceErrorEvent extends AbstractAiServiceEvent implements AiServiceErrorEvent {

    private final Throwable error;

    public DefaultAiServiceErrorEvent(AiServiceErrorEventBuilder builder) {
        super(builder);
        this.error = ensureNotNull(builder.getError(), "error");
    }

    @Override
    public Throwable error() {
        return error;
    }
}
