package com.networknt.agent.observability.event;

import static com.networknt.agent.internal.ValidationUtils.ensureNotNull;

import com.networknt.agent.model.chat.request.ChatRequest;
import com.networknt.agent.observability.api.event.AiServiceRequestIssuedEvent;

/**
 * Default implementation of {@link AiServiceRequestIssuedEvent}.
 */
public class DefaultAiServiceRequestIssuedEvent extends AbstractAiServiceEvent implements AiServiceRequestIssuedEvent {

    private final ChatRequest request;

    public DefaultAiServiceRequestIssuedEvent(AiServiceRequestIssuedEventBuilder builder) {
        super(builder);
        this.request = ensureNotNull(builder.request(), "request");
    }

    @Override
    public ChatRequest request() {
        return request;
    }
}
